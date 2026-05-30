package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserEmailDto;
import pl.wsb.fitnesstracker.user.api.UserSimpleDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Kontroler REST API do zarządzania użytkownikami w systemie.
 * Zhermetyzowany (brak modyfikatora public).
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    // Використовуємо репозиторій напряму, як дозволено в інструкції
    private final UserRepository userRepository;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthdate(), user.getEmail()))
                .toList();
    }

    @GetMapping("/simple")
    public List<UserSimpleDto> getAllSimpleUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserSimpleDto(user.getId(), user.getFirstName(), user.getLastName()))
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthdate(), user.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping("/email")
    public List<UserEmailDto> getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmailContainingIgnoreCase(email).stream()
                .map(user -> new UserEmailDto(user.getId(), user.getEmail()))
                .toList();
    }

    @GetMapping("/older/{time}")
    public List<UserDto> getUsersOlderThan(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate time) {
        return userRepository.findByBirthdateBefore(time).stream()
                .map(user -> new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthdate(), user.getEmail()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = new User(userDto.firstName(), userDto.lastName(), userDto.birthdate(), userDto.email());
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getBirthdate(), savedUser.getEmail());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
    }

    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setBirthdate(userDto.birthdate());
        user.setEmail(userDto.email());

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getBirthdate(), savedUser.getEmail());
    }
}