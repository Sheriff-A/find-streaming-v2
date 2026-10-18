package org.sheriffa.backend.user;

import org.sheriffa.backend.security.RefreshTokenService;
import org.sheriffa.backend.user.dto.MyProfileResponse;
import org.sheriffa.backend.user.dto.PatchProfileRequest;
import org.sheriffa.backend.user.dto.PublicProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                       ProfileRepository profileRepository,
                       RefreshTokenService refreshTokenService,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.refreshTokenService = refreshTokenService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(UUID userId) {
        return toMyProfileResponse(loadProfile(userId));
    }

    @Transactional
    public MyProfileResponse patchMyProfile(UUID userId, PatchProfileRequest request) {
        Profile profile = loadProfile(userId);

        if (request.displayName() != null) {
            profile.setDisplayName(
                    request.displayName()
            );
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(
                    nullIfBlank(request.avatarUrl())
            );
        }
        if (request.bio() != null) {
            profile.setBio(
                    nullIfBlank(request.bio())
            );
        }
        if (request.preferences() != null) {
            profile.setPreferences(
                    objectMapper.writeValueAsString(
                            request.preferences()
                    )
            );
        }

        return toMyProfileResponse(
                profileRepository.saveAndFlush(profile)
        );
    }

    @Transactional
    public void deactivate(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setStatus(UserStatus.INACTIVE);
        refreshTokenService.revokeAllForUser(userId);
    }

    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(UUID userId) {
        Profile profile = loadProfile(userId);
        if (profile.getUser().getStatus() != UserStatus.ACTIVE) {
            throw new UserNotFoundException(userId);
        }
        return new PublicProfileResponse(
                profile.getId(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getBio()
        );
    }

    private Profile loadProfile(UUID userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private static String nullIfBlank(String s) {
        return s.isBlank() ? null : s;
    }

    private MyProfileResponse toMyProfileResponse(Profile profile) {
        User user = profile.getUser();
        return new MyProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(
                        r -> "ROLE_" + r.name()
                ).sorted().toList(),
                user.getStatus(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getBio(),
                objectMapper.readTree(profile.getPreferences()),
                user.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
