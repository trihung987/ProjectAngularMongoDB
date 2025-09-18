package me.trihung.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email"),
		@UniqueConstraint(columnNames = "username") })
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) // only explicitly = true chỉ bao gồm các field có
																		// annotation
																		// không so sánh các super class
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include // Thêm vào khi hash để dùng trong equal hoặc hashCode
	private UUID id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@JsonIgnore // Bỏ qua khi chuyển từ obj thành json và ngược lại
	@Column(name = "password")
	private String password;

//    @Column(name = "phone", unique = true)
//    private String phone;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "full_name")
	private String fullName;

//    @Column(name = "description", columnDefinition = "TEXT")
//    private String description;

//    @Column(name = "birthday")
//    private LocalDate birthday;
	
	//Tự tạo 1 bảng riêng để lưu trữ danh sách role của user
	// có 2 cột userid - role_id (khóa chính là 2 cột )
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@PostLoad
	public void afterLoad() {
		// Logic xử lý sẽ chạy ngay sau khi entity được tải
		System.out.println("Giá trị đã được set sau khi tải");
	}

}
