package com.github.worldsender.mcanm.client.model.mcanmmodel.glcontext;

import com.github.worldsender.mcanm.client.model.mcanmmodel.data.IModelData;
import com.github.worldsender.mcanm.client.model.mcanmmodel.data.ModelDataBasic;
import com.github.worldsender.mcanm.client.model.mcanmmodel.data.RawDataV1;
import com.google.common.base.Optional;

public class GLHelperBasic extends GLHelper {
	@Override
	public Optional<IModelData> preBakeV1(RawDataV1 datav1) {
		return Optional.<IModelData>of(new ModelDataBasic(datav1));
	}

}
