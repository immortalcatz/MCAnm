package com.github.worldsender.mcanm.client.model.mcanmmodel.glcontext;

import net.minecraft.client.renderer.vertex.VertexFormat;

import com.github.worldsender.mcanm.client.model.mcanmmodel.ModelMCMD.MCMDState;
import com.github.worldsender.mcanm.client.model.mcanmmodel.animation.IAnimation;
import com.github.worldsender.mcanm.client.model.mcanmmodel.data.IModelData;
import com.github.worldsender.mcanm.client.model.mcanmmodel.data.RawData;
import com.github.worldsender.mcanm.client.model.mcanmmodel.data.RawDataV1;
import com.github.worldsender.mcanm.client.model.mcanmmodel.data.RenderPassInformation;
import com.github.worldsender.mcanm.client.model.mcanmmodel.loader.VersionizedModelLoader;
import com.github.worldsender.mcanm.client.renderer.IAnimatedObject;
import com.google.common.base.Predicate;

/**
 * Represents an GLHelper. That is a render-glHelper for the correct OpenGL-
 * version present on this computer.
 *
 * @author WorldSEnder
 *
 * @param <T>
 *            the ModelDataType this GLHelper can handle
 */
public abstract class GLHelper {
	protected IModelData currentData;
	protected RenderPassInformation passCache = new RenderPassInformation();
	/**
	 * This method is used to translate the {@link RawDataV1} that was
	 * previously read by the appropriate {@link VersionizedModelLoader}.
	 *
	 * @param amd
	 *            the data loaded by the {@link VersionizedModelLoader}
	 * @return data that can be understood by this {@link GLHelper}
	 */
	public final IModelData preBake(MCMDState state, VertexFormat format) {
		if (state == null)
			throw new IllegalArgumentException("State can not be null");
		RawData amd = state.getData();
		if (amd instanceof RawDataV1)
			return currentData = this.preBakeV1((RawDataV1) amd, format);
		throw new IllegalArgumentException("Unrecognized data format");

	}
	/**
	 * Loads data of version 1.
	 *
	 * @param datav1
	 *            the data to be loaded into this handler
	 */
	public abstract IModelData preBakeV1(RawDataV1 datav1, VertexFormat format);
	/**
	 * Actually renders the model that MAY have been previously loaded. If no
	 * data has been loaded yet, this method is expected to instantly return. It
	 * may log but it is not required and/or expected to do so.
	 *
	 * @param entity
	 *            the entity to render
	 * @param subFrame
	 *            the current subFrame, always 0.0 <= subFrame <= 1.0
	 **/
	public void render(IAnimatedObject object, float subFrame) {
		if (this.currentData == null) // Not loaded correctly
			return;
		if (object == null) // Don't render
			return;
		passCache.reset();
		RenderPassInformation currentPass = object.preRenderCallback(subFrame,
				passCache);
		if (currentPass == null)
			this.currentData.setup(RenderPassInformation.BIND_POSE, 0.0F);
		else {
			IAnimation animation = currentPass.getAnimation();
			float frame = currentPass.getFrame();
			Predicate<String> filter = currentPass.getPartPredicate();
			this.currentData.setup(animation, frame);
			if (filter != null)
				this.currentData.renderFiltered(filter);
			else
				this.currentData.renderAll();
		}
	}
	/**
	 * Selects an appropriate {@link GLHelper} from the known types.
	 */
	public static GLHelper getNewAppropriateHelper() {
		// TODO: enable advanced rendering, write when you feel like you have to
		// optimize
		return new GLHelperBasic();
	}
}
