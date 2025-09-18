package me.trihung.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organizers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Organizer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String logo; // Lưu URL của logo sau khi upload
}