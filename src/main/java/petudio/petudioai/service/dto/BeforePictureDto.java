package petudio.petudioai.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BeforePictureDto {
    private final String originalPictureName;
    private final byte[] byteArray;
}
