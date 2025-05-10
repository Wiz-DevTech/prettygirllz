package com.wizdevtech.identityaccess.service;

import com.wizdevtech.identityaccess.dto.AuthenticationRequest;
import com.wizdevtech.identityaccess.dto.AuthenticationResponse;
import com.wizdevtech.identityaccess.grpc.AuthServiceGrpc;
import com.wizdevtech.identityaccess.grpc.LoginRequest;
import com.wizdevtech.identityaccess.grpc.LoginResponse;
import com.wizdevtech.identityaccess.grpc.User;
import com.wizdevtech.identityaccess.grpc.ValidateTokenRequest;
import com.wizdevtech.identityaccess.grpc.ValidateTokenResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

@GRpcService
@RequiredArgsConstructor
public class AuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthenticationService authService;
    private final JwtService jwtService;

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            AuthenticationRequest authRequest = AuthenticationRequest.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .build();

            AuthenticationResponse authResponse = authService.authenticate(authRequest);

            User userGrpc = User.newBuilder()
                    .setId(authResponse.getId())
                    .setEmail(authResponse.getEmail())
                    .addAllRoles(authResponse.getRoles())
                    .build();

            LoginResponse response = LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setToken(authResponse.getToken())
                    .setUser(userGrpc)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (BadCredentialsException e) {
            responseObserver.onNext(LoginResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid credentials")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        try {
            String token = request.getToken();
            boolean isValid = false;
            User userGrpc = null;

            try {
                var claims = jwtService.extractAllClaims(token);
                if (!jwtService.isTokenExpired(token)) {
                    isValid = true;
                    long userId = Long.parseLong(jwtService.extractUsername(token));
                    String email = claims.get("email", String.class);

                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get("roles", List.class);

                    userGrpc = User.newBuilder()
                            .setId(userId)
                            .setEmail(email)
                            .addAllRoles(roles)
                            .build();
                }
            } catch (Exception e) {
                isValid = false;
            }

            ValidateTokenResponse.Builder responseBuilder = ValidateTokenResponse.newBuilder()
                    .setIsValid(isValid);

            if (userGrpc != null) {
                responseBuilder.setUser(userGrpc);
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}