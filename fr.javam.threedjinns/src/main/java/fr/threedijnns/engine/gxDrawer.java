package fr.threedijnns.engine;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import fr.java.math.algebra.matrix.generic.Matrix44D;
import fr.java.maths.algebra.matrices.DoubleMatrix44;
import fr.threedijnns.api.lang.enums.DrawBuffer;
import fr.threedijnns.api.lang.enums.MatrixType;

public interface gxDrawer {

	// Buffer selection methods
	public void 		setDrawBuffer	(DrawBuffer _drawbuffer);

	// Clear Option Parameters
	public void 		clearBuffer		(DrawBuffer _drawbuffer);
	public void 		clearAllBuffers	();

	public int  		clearColor		();
	public void 		clearColor		(int _color);
	public void 		clearColor		(byte _red, byte _green, byte _blue, byte _alpha);
	public void 		clearColor		(int _red, int _green, int _blue, int _alpha);
	public void 		clearColor		(float _red, float _green, float _blue, float _alpha);
	public void 		clearColor		(double _red, double _green, double _blue, double _alpha);
	public void 		clearColor		(FloatBuffer _fb);
	public void 		clearColor		(DoubleBuffer _db);

	// Rendering methods
	public void 		beginRender		();
	public void 		endRender		();

	public void 		swapBuffer		();

	// Matrix methods
	public void 		pushMatrix		(MatrixType _type);
	public void 		pushMatrix		(MatrixType _type, int _texId);
	public DoubleMatrix44 	getMatrix		(MatrixType _type);
	public DoubleMatrix44 	getMatrix		(MatrixType _type, int _texId);
	public void 		loadMatrix		(MatrixType _type, DoubleMatrix44 _matrix);
	public void 		loadMatrix		(MatrixType _type, DoubleMatrix44 _matrix, int _texId);
	public void 		loadMatrixMult	(MatrixType _type, Matrix44D _matrix);
	public void 		loadMatrixMult	(MatrixType _type, DoubleMatrix44 _matrix, int _texId);
	public void 		popMatrix		(MatrixType _type);
	public void 		popMatrix		(MatrixType _type, int _texId);

}
