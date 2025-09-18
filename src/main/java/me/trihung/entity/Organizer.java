package me.trihung.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "organizers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Organizer extends BaseEntity {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String name;

    private String bio;

    private String logo; // Lưu URL của logo sau khi upload
}