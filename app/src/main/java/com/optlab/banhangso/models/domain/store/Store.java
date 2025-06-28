package com.optlab.banhangso.models.domain.store;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;
}
