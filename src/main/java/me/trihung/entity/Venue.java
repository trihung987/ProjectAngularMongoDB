package me.trihung.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "venues")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Venue extends BaseEntity {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String province;
    private String address;
}