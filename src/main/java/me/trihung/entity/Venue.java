package me.trihung.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venues")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Venue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String province;
    private String address;
}