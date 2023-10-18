package petudio.petudioai.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PictureServiceDto {
    private final String originalPictureName;
    private final byte[] byteArray;
}
