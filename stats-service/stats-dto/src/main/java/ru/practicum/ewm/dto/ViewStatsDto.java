package ru.practicum.ewm.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {

    String app;
    String uri;
    Long hits;
}
