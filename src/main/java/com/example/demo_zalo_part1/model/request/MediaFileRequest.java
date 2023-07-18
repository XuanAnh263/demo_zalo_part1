package com.example.demo_zalo_part1.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Builder
@ToString

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaFileRequest {


    PostRequest post;
    FileRequest file;
    UserRequest creatorId;
}
