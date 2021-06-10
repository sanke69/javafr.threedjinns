package fr.threedijnns.api.attributes;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;

import fr.java.lang.enums.PixelFormat;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.java.maths.geometry.plane.types.SimpleDimension2D;
import fr.media.image.utils.BufferedImages;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.texture.TextureBase;
import fr.threedijnns.api.lang.enums.FrameType;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.utils.MipMapUtils;

public class TextureCubeMap {

	TextureBase textureBase;

	public TextureCubeMap() {
		super();

		textureBase = null;
	}
	public TextureCubeMap(int _w, int _h, PixelFormat _pxl_fmt, boolean _mipmap) {
		super();

		textureBase = null;
		if(_w != 0 && _h != 0)
			createTextureCubeMapBase(_w, _h, _pxl_fmt);
	}

	public TextureBase getTextureBase() {
		return textureBase;
	}

	public void enable(int _unit) {
		if(textureBase != null)
			textureBase.enable(_unit);
	}
	public void disable(int _unit) {
		if(textureBase != null)
			textureBase.disable(_unit);
	}

	public void loadFromCubeMapFile(String _file, PixelFormat _pxl_fmt) {
		BufferedImage[] cubeMap = new BufferedImage[6];

		boolean isOK = false;
		try(BufferedReader br = new BufferedReader(new FileReader(new File(_file)))) {

			String line = null;
			while((line = br.readLine()) != null) {
				System.out.println(line);

				if(line.contains(";"))
					; // Comments
				else if(line.contains("[eof]"))
					break;
				else if(line.contains("[") && line.contains("]")) {
					///< SECTION LINE >
					line = line.substring(line.indexOf('[') + 1, line.lastIndexOf(']'));
					line = line.trim();

					if(line.compareToIgnoreCase("CubeMap") == 0)
						isOK = true;

				} else if(line.contains("=")) {
					// KEY LINE
					int pequal   = line.indexOf('=');
					int pcomment = line.indexOf(';');
					pcomment = pcomment < pequal ? line.length() + 1 : pcomment;

					String key = line.substring(0, pequal - 1);
					key = key.trim();

					String texture = line.substring(pequal + 1, pcomment - 1);
					texture = texture.trim();

					if(isOK) {
						String path = _file.substring(0, _file.lastIndexOf('/')) + "/" + texture;
						BufferedImage textureImage = ImageIO.read(new File(path));

						switch (key) {
						case "NegativeX":	cubeMap[0] = textureImage;	break;
						case "PositiveX":	cubeMap[1] = textureImage;	break;
						case "NegativeY":	cubeMap[2] = textureImage;	break;
						case "PositiveY":	cubeMap[3] = textureImage;	break;
						case "NegativeZ":	cubeMap[4] = textureImage;	break;
						case "PositiveZ":	cubeMap[5] = textureImage;	break;
						}

					} else {
						// FREE LINE
					}
				}

			}

			gx.runLater(() -> {
			createTextureCubeMapBase(cubeMap[0].getWidth(), cubeMap[0].getHeight(), _pxl_fmt);
			updateFromImage(FrameType.TEX_CUBEMAP_XN, cubeMap[0], null, null);
			updateFromImage(FrameType.TEX_CUBEMAP_XP, cubeMap[1], null, null);
			updateFromImage(FrameType.TEX_CUBEMAP_YN, cubeMap[2], null, null);
			updateFromImage(FrameType.TEX_CUBEMAP_YP, cubeMap[3], null, null);
			updateFromImage(FrameType.TEX_CUBEMAP_ZN, cubeMap[4], null, null);
			updateFromImage(FrameType.TEX_CUBEMAP_ZP, cubeMap[5], null, null);
			});

		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void updateFromImage(FrameType _frame, BufferedImage _image, SimpleRectangle2D _in, SimpleRectangle2D _out) {
		updateTextureBase(_frame, _image.getWidth(), _image.getHeight(), getTextureBase().getPixelFormat(), BufferedImages.getPixelBuffer(_image, getTextureBase().getPixelFormat()));
	}

	public void updateFromBuffer(FrameType _frame, Buffer _buffer, int _w, int _h, PixelFormat _pxl_fmt, SimpleRectangle2D _in, SimpleRectangle2D _out) {
		updateTextureBase(_frame, _w, _h, _pxl_fmt, _buffer);
	}

	public void createTextureCubeMapBase(int _w, int _h, PixelFormat _pxl_fmt) {
		if(textureBase != null) {
			gx.log.error("Trying to create TextureBase over a existent instance.");
		} else {
			int w      = MipMapUtils.nearestPowerOfTwo(_w),
				h      = MipMapUtils.nearestPowerOfTwo(_h),
				pxl_sz = _pxl_fmt.bytesPerPixel();

			textureBase = gx.createTextureCubeMap(SimpleDimension2D.of(w, h), _pxl_fmt, null);
		}
	}
	public void updateTextureBase(FrameType _frame, int _w, int _h, PixelFormat _pxl_fmt, Buffer _buffer) {
		if(textureBase == null) {
			gx.log.error("Trying to update a non existent TextureBase.");
		} else {
			textureBase.setActiveFrame(_frame);
			textureBase.update(0, _w * _h * _pxl_fmt.bytesPerPixel(), LockFlag.WriteOnly, _buffer);
			textureBase.setActiveFrame(FrameType.NoId);
		}
	}

}
