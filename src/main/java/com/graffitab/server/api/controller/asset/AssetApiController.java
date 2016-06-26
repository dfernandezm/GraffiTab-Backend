package com.graffitab.server.api.controller.asset;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.graffitab.server.api.dto.asset.AssetDto;
import com.graffitab.server.api.dto.asset.result.AssetResult;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.asset.Asset;
import com.graffitab.server.service.asset.AssetService;

/**
 * Created by david on 26/06/2016.
 */
@RestController
@RequestMapping("/api/assets")
public class AssetApiController {

    @Resource
    private AssetService assetService;

    @Resource
    private OrikaMapper mapper;

    @RequestMapping(value = {"/{guid}/progress"}, method = RequestMethod.GET)
    public AssetResult getAssetProgress(@PathVariable("guid") String assetGuid) {
        AssetResult assetResult = new AssetResult();
        Asset asset = assetService.getAsset(assetGuid);
        assetResult.setAsset(mapper.map(asset, AssetDto.class));
        return assetResult;
    }
}
