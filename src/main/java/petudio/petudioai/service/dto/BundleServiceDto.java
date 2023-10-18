package petudio.petudioai.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class BundleServiceDto {
    private final Long bundleId;
    private final List<PictureServiceDto> pictures;
}
