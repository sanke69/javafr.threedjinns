package fr.java.io.file.format.max;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class Loader3DS {
	public static final int CHUNK_VERSION  = 0x0002;
	public static final int 	CHUNK_RGBF      = 0x0010;
	public static final int 	CHUNK_RGBB      = 0x0011;

	public static final int CHUNK_PERCENTW  = 0x0030;
	public static final int CHUNK_PERCENTF  = 0x0031;

	public static final int CHUNK_PRJ       = 0xC23D;
	public static final int CHUNK_MLI       = 0x3DAA;

	public static final int CHUNK_MAIN      = 0x4D4D;
	public static final int 	CHUNK_OBJMESH   = 0x3D3D;
	public static final int 	CHUNK_ONEUNIT  = 0x0100;
	public static final int 		CHUNK_BKGCOLOR  = 0x1200;
	public static final int 		CHUNK_AMBCOLOR  = 0x2100;
	public static final int 	CHUNK_DEFAULT_VIEW = 0x3000;
	public static final int 		CHUNK_VIEW_TOP = 0x3010;
	public static final int 		CHUNK_VIEW_BOTTOM = 0x3020;
	public static final int 		CHUNK_VIEW_LEFT = 0x3030;
	public static final int 		CHUNK_VIEW_RIGHT = 0x3040;
	public static final int 		CHUNK_VIEW_FRONT = 0x3050;
	public static final int 		CHUNK_VIEW_BACK = 0x3060;
	public static final int 		CHUNK_VIEW_USER = 0x3070;
	public static final int 		CHUNK_VIEW_CAMERA = 0x3080;
	public static final int 			CHUNK_OBJBLOCK  = 0x4000;
	public static final int 				CHUNK_TRIMESH   = 0x4100;
	public static final int 					CHUNK_VERTLIST  = 0x4110;
	public static final int 					CHUNK_VERTFLAGS = 0x4111;
	public static final int 					CHUNK_FACELIST  = 0x4120;
	public static final int 					CHUNK_FACEMAT   = 0x4130;
	public static final int 					CHUNK_MAPLIST   = 0x4140;
	public static final int 					CHUNK_SMOOLIST  = 0x4150;
	public static final int 					CHUNK_TRMATRIX  = 0x4160;
	public static final int 					CHUNK_MESHCOLOR = 0x4165;
	public static final int 					CHUNK_TXTINFO   = 0x4170;
	public static final int 				CHUNK_LIGHT     = 0x4600;
	public static final int 			CHUNK_SPOTLIGHT = 0x4610;
	public static final int 		CHUNK_CAMERA    = 0x4700;
	public static final int 	CHUNK_HIERARCHY = 0x4F00;

	public static final int CHUNK_VIEWPORT_LAYOUT_OLD  = 0x7000;
	public static final int 	CHUNK_VIEWPORT_DATA_OLD  = 0x7010;
	public static final int 	CHUNK_VIEWPORT_SIZE = 0x7020;
	public static final int 		CHUNK_NETWORK_VIEW = 0X7030;

	public static final int CHUNK_VIEWPORT_LAYOUT  = 0x7001;
	public static final int 	CHUNK_VIEWPORT_DATA  = 0x7011;
	public static final int 	CHUNK_VIEWPORT_DATA3 = 0x7012;

	public static final int CHUNK_MATERIAL  = 0xAFFF;
	public static final int 	CHUNK_MATNAME   = 0xA000;
	public static final int 	CHUNK_AMBIENT   = 0xA010;
	public static final int 	CHUNK_DIFFUSE   = 0xA020;
	public static final int 	CHUNK_SPECULAR  = 0xA030;
	public static final int 	CHUNK_TEXTURE   = 0xA200;
	public static final int 	CHUNK_BUMPMAP   = 0xA230;
	public static final int 	CHUNK_MAPFILE   = 0xA300;
	public static final int CHUNK_KEYFRAMER = 0xB000;
	public static final int 	CHUNK_AMBIENTKEY    = 0xB001;
	public static final int 	CHUNK_TRACKINFO = 0xB002;
	public static final int 		CHUNK_TRACKOBJNAME  = 0xB010;
	public static final int 		CHUNK_TRACKPIVOT    = 0xB013;
	public static final int 		CHUNK_TRACKPOS      = 0xB020;
	public static final int 		CHUNK_TRACKROTATE   = 0xB021;
	public static final int 		CHUNK_TRACKSCALE    = 0xB022;
	public static final int 		CHUNK_TRACKMORPH    = 0xB026;
	public static final int 		CHUNK_TRACKHIDE     = 0xB029;
	public static final int 		CHUNK_OBJNUMBER     = 0xB030;
	public static final int 	CHUNK_TRACKCAMERA = 0xB003;
	public static final int 		CHUNK_TRACKFOV  = 0xB023;
	public static final int 		CHUNK_TRACKROLL = 0xB024;
	public static final int 	CHUNK_TRACKCAMTGT = 0xB004;
	public static final int 	CHUNK_TRACKLIGHT  = 0xB005;
	public static final int 	CHUNK_TRACKLIGTGT = 0xB006;
	public static final int 	CHUNK_TRACKSPOTL  = 0xB007;
	public static final int 	CHUNK_FRAMES      = 0xB008;

	class Chunk {
		static final int sizeof = Short.BYTES + Long.BYTES;

		short Flag;
		long Size;

		Chunk(ByteBuffer _block) {
			Flag = _block.getShort();
			Size = _block.getLong();
		}
	}

	class ViewPortLayout {
		short style;
		short active;
		short unknow1;
		short swap;
		short unknow2;
		short swap_prior;
		short swap_view;

		ViewPortLayout(short Style, short Active, short Unknow1, short Swap, short Unknow2, short Swap_prior,
				short Swap_view) {
			style = Style;
			active = Active;
			unknow1 = Unknow1;
			swap = Swap;
			unknow2 = Unknow2;
			swap_prior = Swap_prior;
			swap_view = Swap_view;

		}
	};

	class ViewPortData {
		short flags, axis_lockout;
		short win_x, win_y, win_w, win_h, win_view;
		float zoom;
		float worldcenter_x, worldcenter_y, worldcenter_z;
		float horiz_ang, vert_ang;
		String camera_name;

		ViewPortData(short Flags, short Axis_lockout, short Win_x, short Win_y, short Win_w, short Win_h,
				short Win_view, float Zoom, float Worldcenter_x, float Worldcenter_y, float Worldcenter_z,
				float Horiz_ang, float Vert_ang, String CameraName) {
			flags = Flags;
			axis_lockout = Axis_lockout;
			win_x = Win_x;
			win_y = Win_y;
			win_w = Win_w;
			win_h = Win_h;
			win_view = Win_view;
			zoom = Zoom;
			worldcenter_x = Worldcenter_x;
			worldcenter_y = Worldcenter_y;
			worldcenter_z = Worldcenter_z;
			horiz_ang = Horiz_ang;
			vert_ang = Vert_ang;
			camera_name = CameraName;
		}
	}

	class Transform3dsMatrix {
		float _11, _12, _13;
		float _21, _22, _23;
		float _31, _32, _33;
	};

	class Translate3dsMatrix {
		float _11, _12, _13;
	};

	RandomAccessFile file_3ds;

	long PC; // File Pointer
	long mFileSize;

	public Loader3DS() {
		super();
	}

	void ProcessFile(String FileName) throws IOException {
		file_3ds = new RandomAccessFile(FileName, "b");
		mFileSize = file_3ds.length();

		ParseChunks();
		EndReading();
	}

	boolean ParseChunks() throws IOException {
		ByteBuffer block;

		Chunk chunk;

		if ((block = ReadBlock(Chunk.sizeof)) == null)
			return false;

		chunk = new Chunk(block);

		switch (chunk.Flag) {
		case CHUNK_RGBF:
			// cout << "Chunk RGBF detected" << std::endl;
			ReadRGBF();
			ParseChunks();
			break;
		case CHUNK_RGBB:
			// cout << "Chunk RGBB detected" << std::endl;
			ReadRGBB();
			ParseChunks();
			break;
		case CHUNK_MAIN:
			// cout << "Chunk Main detected" << std::endl;
			ParseChunks();
			break;
		case CHUNK_OBJMESH:
			// cout << "Chunk Object detected" << std::endl;
			ReadObjChunk();
			ParseChunks();
			break;
		case CHUNK_OBJBLOCK:
			// cout << "Chunk Object Block detected" << std::endl;
			ReadObjBlock();
			// cout << std::endl;
			ParseChunks();
			break;
		case CHUNK_TRIMESH:
			// cout << "Chunk Trimesh detected" << std::endl;
			ParseChunks();
			break;
		case CHUNK_VERTLIST:
			// cout << "Chunk VertList detected at " << (PC - sizeof(chunk3ds))
			// << std::endl;
			ReadVertList();
			ParseChunks();
			break;
		case CHUNK_FACELIST:
			// cout << "Chunk FaceList detected" << std::endl;
			ReadFaceList();
			ParseChunks();
			break;
		case CHUNK_FACEMAT:
			// cout << "Chunk FaceMat detected" << std::endl;
			ReadFaceMat();
			ParseChunks();
			break;
		case CHUNK_MAPLIST:
			// cout << "Chunk MapList detected" << std::endl;
			ReadMapList();
			ParseChunks();
			break;
		case CHUNK_SMOOLIST:
			// cout << "Chunk SmoothList detected" << std::endl;
			// ReadSmoothList(); // Still unsupported
			SkipChunk(chunk);
			ParseChunks();
			break;
		case CHUNK_TRMATRIX:
			// cout << "Chunk TRMatrix detected" << std::endl;
			ReadTrMatrix();
			ParseChunks();
			break;
		case CHUNK_LIGHT:
			// cout << "Chunk Light detected" << std::endl;
			ReadLight();
			ParseChunks();
			break;
		case CHUNK_SPOTLIGHT:
			// cout << "Chunk SpotLight detected" << std::endl;
			ReadSpotLight();
			ParseChunks();
			break;
		case CHUNK_CAMERA:
			// cout << "Chunk Camera detected" << std::endl;
			ReadCamera();
			ParseChunks();
			break;
		case (short) CHUNK_MATERIAL:
			// cout << "Chunk Material detected" << std::endl;
			ParseChunks();
			break;
		case (short) CHUNK_MATNAME:
			// cout << "Chunk MatName detected" << std::endl;
			ReadMatName();
			ParseChunks();
			break;
		case (short) CHUNK_MAPFILE:
			// cout << "Chunk MapFile detected" << std::endl;
			ReadMapFile();
			ParseChunks();
			break;
		case (short) CHUNK_KEYFRAMER:
			// cout << "Chunk Keyframer detected" << std::endl;
			ParseChunks();
			break;
		case (short) CHUNK_TRACKINFO:
			// cout << "Chunk TrackInfo detected" << std::endl;
			ParseChunks();
			break;
		case (short) CHUNK_TRACKOBJNAME:
			// cout << "Chunk TrackObjName detected" << std::endl;
			ReadTrackObjName();
			ParseChunks();
			break;
		case (short) CHUNK_TRACKPOS:
			// cout << "Chunk TrackPos detected" << std::endl;
			ReadTrackPos();
			ParseChunks();
			break;
		case (short) CHUNK_TRACKROTATE:
			// cout << "Chunk TrackRotate detected" << std::endl;
			ReadTrackRot();
			ParseChunks();
			break;
		case (short) CHUNK_TRACKSCALE:
			// cout << "Chunk TrackScale detected" << std::endl;
			ReadTrackScale();
			ParseChunks();
			break;
		case (short) CHUNK_OBJNUMBER:
			// cout << "Chunk ObjNumber detected" << std::endl;
			ReadObjNumber();
			ParseChunks();
			break;
		default:
			// cout << "Unknown Chunk at " << file_3ds.tellg() << " offset" <<
			// std::endl;
			SkipChunk(chunk);
			ParseChunks();
			break;
		}
		return true;
	}

	void EndReading() {
		UserEndOfFile();
	}

	boolean SkipChunk(Chunk chunk) throws IOException {
		PC += (chunk.Size - Chunk.sizeof);
		file_3ds.seek(PC);
		if (PC >= mFileSize)
			return false;
		return true;
	}

	ByteBuffer ReadBlock(int _size) throws IOException {
		byte[] block = new byte[_size];

		PC = file_3ds.getFilePointer();
		if (file_3ds.read(block) != _size)
			return null;

		PC += _size;
		file_3ds.seek(PC);
		return ByteBuffer.wrap(block);
	}

	String ReadASCII() throws IOException {
		byte[] block = new byte[255];
		int c, i = 0;
		long Count = 0;

		do {
			c = file_3ds.read();
			block[i++] = (byte) (c & 0xFF);
			Count++;
		} while (c != 0);

		PC += Count;
		file_3ds.seek(PC);

		return new String(block, StandardCharsets.UTF_8);
	}

	void movePC(long forward) throws IOException {
		PC += forward;
		file_3ds.seek(PC);
	}

	void ReadRGBF() throws IOException {
		ByteBuffer block;
		float[] c = new float[3];
		if ((block = ReadBlock(3 * Float.BYTES)) == null)
			return;
	}

	void ReadRGBB() throws IOException {
		ByteBuffer block;
		char[] c = new char[3];
		if ((block = ReadBlock(3 * Byte.BYTES)) == null)
			return;
	}

	void ReadObjBlock() throws IOException {
		UserObjName(ReadASCII());
	}

	void ReadCamera() throws IOException {
		ByteBuffer block;
		float[] c = new float[8];
		if ((block = ReadBlock(8 * Float.BYTES)) == null)
			return;
		UserCamera(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7]);
	}

	void ReadFaceMat() throws IOException {
		short n, nf;
		String MaterialName;

		MaterialName = ReadASCII();

		n = ReadBlock(Short.BYTES).getShort();

		while (n-- > 0) { // Material number assigned to any face
			nf = ReadBlock(Short.BYTES).getShort();

			UserFaceMaterial(MaterialName, nf);
		}
	}

	void ReadFrames() throws IOException {
		ByteBuffer block;
		long[] c = new long[2];
		if ((block = ReadBlock(2 * Long.BYTES)) != null)
			return;
		UserFrames(c[0], c[1]);
	}

	void ReadLight() throws IOException {
		ByteBuffer block;
		byte[] c = new byte[3];
		if ((block = ReadBlock(3 * Long.BYTES)) != null)
			return;
		UserLight(c[0], c[1], c[2]);
	}

	void ReadMapFile() throws IOException {
		String fileName = ReadASCII();
		UserMapFile(fileName);
	}

	void ReadMapList() throws IOException {
		ByteBuffer block;
		short nv;
		float[] c = new float[2];

		if ((block = ReadBlock(Short.BYTES)) == null)
			return;

		nv = block.getShort();

		while (nv-- > 0) {
			if ((block = ReadBlock(2 * Float.BYTES)) == null)
				return;
			c[0] = block.getFloat();
			c[1] = block.getFloat();
			UserMapVertex(c[0], c[1]);
		}
	}

	void ReadMatName() throws IOException {
		String matName;

		matName = ReadASCII();
		UserMatName(matName);
	}

	void ReadObjNumber() throws IOException {
		short n;

		n = ReadBlock(Short.BYTES).getShort();
		UserObjNumber((int) n);
	}

	void ReadSpotLight() throws IOException {
		ByteBuffer block;
		float[] c = new float[5];
		block = ReadBlock(5 * Float.BYTES);
		for (int i = 0; i < 5; ++i)
			c[i] = block.getFloat();
		UserSpotLight(c[0], c[1], c[2], c[3], c[4]);
	}

	void ReadTrackObjName() throws IOException {
		ByteBuffer block;
		short[] w = new short[3];

		// Read ASCIIZ name
		String trackName = ReadASCII();

		block = ReadBlock(3 * Short.BYTES);
		for (int i = 0; i < 3; ++i)
			w[i] = block.getShort();

		UserTrackObjectName(trackName, w[0], w[1], w[2]);
	}

	void ReadTrackPos() throws IOException {
		ByteBuffer block;
		short n, nf;
		float[] pos = new float[3];
		long flags;

		file_3ds.seek(file_3ds.getFilePointer() + 10);
		PC += 10;

		n = ReadBlock(Short.BYTES).getShort();

		file_3ds.seek(file_3ds.getFilePointer() + 2);
		PC += 2;
		while (n-- > 0) {
			int i;
			float dat;

			nf = ReadBlock(Short.BYTES).getShort();
			flags = ReadBlock(Long.BYTES).getShort();

			for (i = 0; i < 32; i++)
				if ((flags & (1 << i)) != 0)
					dat = ReadBlock(Float.BYTES).getFloat();

			block = ReadBlock(3 * Float.BYTES);
			for (int j = 0; j < 3; ++j)
				pos[j] = block.getFloat();

			UserTrackPos(nf, flags, pos[0], pos[1], pos[2]);
		}
	}

	void ReadTrackRot() throws IOException {
		ByteBuffer block;
		short n, nf;
		float[] pos = new float[4];
		long flags;

		file_3ds.seek(file_3ds.getFilePointer() + 10);
		PC += 10;

		n = ReadBlock(Short.BYTES).getShort();

		file_3ds.seek(file_3ds.getFilePointer() + 2);
		PC += 2;
		while (n-- > 0) {
			int i;
			float dat;
			nf = ReadBlock(Short.BYTES).getShort();
			flags = ReadBlock(Long.BYTES).getLong();

			for (i = 0; i < 32; i++)
				if ((flags & (1 << i)) != 0)
					dat = ReadBlock(Float.BYTES).getFloat();

			block = ReadBlock(4 * Float.BYTES);
			for (int j = 0; j < 4; ++j)
				pos[j] = block.getFloat();

			UserTrackRot(nf, flags, pos[0], pos[1], pos[2]);
		}
	}

	void ReadTrackScale() throws IOException {
		ByteBuffer block;
		short n, nf;
		float[] pos = new float[3];
		long flags;

		file_3ds.seek(file_3ds.getFilePointer() + 10);
		PC += 10;

		n = ReadBlock(Short.BYTES).getShort();

		file_3ds.seek(file_3ds.getFilePointer() + 2);
		PC += 2;
		while (n-- > 0) {
			int i;
			float dat;
			nf = ReadBlock(Short.BYTES).getShort();
			flags = ReadBlock(Long.BYTES).getLong();

			for (i = 0; i < 32; i++)
				if ((flags & (1 << i)) != 0)
					dat = ReadBlock(Float.BYTES).getFloat();

			block = ReadBlock(3 * Float.BYTES);
			for (int j = 0; j < 3; ++j)
				pos[j] = block.getFloat();
			UserTrackScale(nf, flags, pos[0], pos[1], pos[2]);
		}
	}

	void ReadTrMatrix() throws IOException {
		ByteBuffer block;
		float[] rot = new float[9];
		float[] trans = new float[3];

		block = ReadBlock(9 * Float.BYTES);
		for (int j = 0; j < 9; ++j)
			rot[j] = block.getFloat();

		block = ReadBlock(3 * Float.BYTES);
		for (int j = 0; j < 3; ++j)
			trans[j] = block.getFloat();

		Transform3dsMatrix Transform = new Transform3dsMatrix();
		Translate3dsMatrix Translate = new Translate3dsMatrix();
		Transform._11 = rot[0];
		Transform._12 = rot[1];
		Transform._13 = rot[2];
		Transform._21 = rot[3];
		Transform._22 = rot[4];
		Transform._23 = rot[5];
		Transform._31 = rot[6];
		Transform._32 = rot[7];
		Transform._33 = rot[8];
		Translate._11 = trans[0];
		Translate._12 = trans[1];
		Translate._13 = trans[2];
		UserTransformMatrix(Transform, Translate);
	}

	void ReadVertList() throws IOException {
		ByteBuffer block;
		short nv;

		nv = ReadBlock(Short.BYTES).getShort();

		while (nv-- > 0) {
			block = ReadBlock(3 * Float.BYTES);
			User3dVert(block.getFloat(), block.getFloat(), block.getFloat());
		}
	}

	void ReadFaceList() throws IOException {
		ByteBuffer block;
		short nv;

		nv = ReadBlock(Short.BYTES).getShort();

		while (nv-- > 0) {
			block = ReadBlock(4 * Short.BYTES);
			User3dFace(block.getShort(), block.getShort(), block.getShort(), block.getShort());
		}

	}

	void ReadObjChunk() {
		UserChunkObj();
	}

	void ReadOneUnit() throws IOException {
		float OneUnit = ReadBlock(Float.BYTES).getFloat();
		UserOneUnit(OneUnit);
	}

	void ReadViewPortLayout() throws IOException {
		short style;
		short active, unknow1, swap, unknow2, swap_prior, swap_view;

		style = ReadBlock(Short.BYTES).getShort();
		active = ReadBlock(Short.BYTES).getShort();
		unknow1 = ReadBlock(Short.BYTES).getShort();
		swap = ReadBlock(Short.BYTES).getShort();
		unknow2 = ReadBlock(Short.BYTES).getShort();
		swap_prior = ReadBlock(Short.BYTES).getShort();
		swap_view = ReadBlock(Short.BYTES).getShort();

		ViewPortLayout viewtmp = new ViewPortLayout(style, active, unknow1, swap, unknow2, swap_prior, swap_view);
		UserViewPortLayout(viewtmp);
	}

	void ReadViewPortSize() throws IOException {
		short PosX, PosY, Width, Height;
		PosX = ReadBlock(Short.BYTES).getShort();
		PosY = ReadBlock(Short.BYTES).getShort();
		Width = ReadBlock(Short.BYTES).getShort();
		Height = ReadBlock(Short.BYTES).getShort();
		UserViewPortSize(PosX, PosY, Width, Height);
	}

	void ReadViewPortData() throws IOException {
		short flags, axis_lockout;
		short win_x, win_y, win_w, win_h, win_view;
		float zoom;
		float worldcenter_x, worldcenter_y, worldcenter_z;
		float horiz_ang, vert_ang;
		String camera_name;

		flags = ReadBlock(Short.BYTES).getShort();
		axis_lockout = ReadBlock(Short.BYTES).getShort();
		win_x = ReadBlock(Short.BYTES).getShort();
		win_y = ReadBlock(Short.BYTES).getShort();
		win_w = ReadBlock(Short.BYTES).getShort();
		win_h = ReadBlock(Short.BYTES).getShort();
		win_view = ReadBlock(Short.BYTES).getShort();

		zoom = ReadBlock(Float.BYTES).getFloat();
		worldcenter_x = ReadBlock(Float.BYTES).getFloat();
		worldcenter_y = ReadBlock(Float.BYTES).getFloat();
		worldcenter_z = ReadBlock(Float.BYTES).getFloat();
		horiz_ang = ReadBlock(Float.BYTES).getFloat();
		vert_ang = ReadBlock(Float.BYTES).getFloat();

		camera_name = ReadASCII();

		ViewPortData vporttmp = new ViewPortData(flags, axis_lockout, win_x, win_y, win_w, win_h, win_view, zoom,
				worldcenter_x, worldcenter_y, worldcenter_z, horiz_ang, vert_ang, camera_name);
		UserViewPortData(vporttmp);
	}

	void ReadViewUser() throws IOException {
		ByteBuffer block;
		float[] pos = new float[3];
		float Width, XYangle, YZangle, BackAngle;

		block = ReadBlock(3 * Float.BYTES);
		for (int j = 0; j < 3; ++j)
			pos[j] = block.getFloat();

		Width = ReadBlock(Float.BYTES).getFloat();
		XYangle = ReadBlock(Float.BYTES).getFloat();
		YZangle = ReadBlock(Float.BYTES).getFloat();
		BackAngle = ReadBlock(Float.BYTES).getFloat();

		UserViewUser(pos[0], pos[1], pos[2], Width, XYangle, YZangle, BackAngle);
	}

	void ReadViewCamera() throws IOException {
		byte[] camera_name = new byte[12];

		ByteBuffer block = ReadBlock(12);
		// UserViewCamera(camera_name);
	}

	boolean isOk() {
		if (file_3ds != null)
			return false;
		return true;
	}

	// For Heritage and Object Implementation
	void UserObjName(String Name) {
		// cout << "Object Name: " << Name << std::endl;
		;
	}

	void User3dVert(float x, float y, float z) {
		// cout << "X: " << x << " Y: " << y << " Z:" << z << std::endl;
		;
	}

	void User3dFace(short A, short B, short C, short Flags) {
		// cout << "A: " << A << " B: " << B << " C:" << C << std::endl;
		// cout << " Flags: " << Flags << std::endl;
		;
	}

	void UserCamera(float px, float py, float pz, float tx, float ty, float tz, float Bank, float Lens) {
		// cout << "Position: X:" << px << "Y: " << py << "Z: " << pz <<
		// std::endl;
		// cout << "Target: X:" << tx << "Y: " << ty << "Z: " << tz <<
		// std::endl;
		// cout << "Bank: " << Bank << std::endl;
		// cout << "Lens: " << Lens << std::endl;
		;
	}

	void UserFaceMaterial(String Name, int Number) {
		// cout << "Name: " << Name << std::endl;
		// cout << "Face Number: " << Number << std::endl;
		;
	}

	void UserMapVertex(float U, float V) {
		// cout << "U: " << U << " V: " << V << std::endl;
		;
	}

	void UserTransformMatrix(Transform3dsMatrix Transform, Translate3dsMatrix Translate) {
		// cout << "Transformation Matrix:" << std::endl;
		// cout << Transform._11 << " " << Transform._12 << " " << Transform._13
		// << " " << std::endl;
		// cout << Transform._21 << " " << Transform._22 << " " << Transform._23
		// << " " << std::endl;
		// cout << Transform._31 << " " << Transform._32 << " " << Transform._33
		// << " " << std::endl;
		// cout << "Translation Matrix:" << std::endl;
		// cout << Translate._11 << " " << Translate._12 << " " << Translate._13
		// << " " << std::endl;
		;
	}

	void UserLight(float x, float y, float z) {
		// cout << "X: " << x << " Y: " << y << " Z: " << z << std::endl;
		;
	}

	void UserSpotLight(float x, float y, float z, float HotSpot, float FallOff) {
		// cout << "X: " << x << " Y: " << y << " Z: <" << z << std::endl;
		// cout << "HotSpot: " << HotSpot << std::endl;
		// cout << "FallOff: " << FallOff << std::endl;
		;
	}

	void UserMatName(String Name) {
		// cout << "Material Name: " << Name << std::endl;
		;
	}

	void UserMapFile(String FileName) {
		// cout << "Texture FileName: " << FileName << std::endl;
		;
	}

	void UserFrames(long Start, long End) {
		// cout << "Start: " << Start << " End: " << End << std::endl;
		;
	}

	void UserTrackObjectName(String Name, int Key1, int Key2, int Key3) {
		// cout << "Track Object Name: " << Name << std::endl;
		// cout << "First Key: " << Key1 << std::endl;
		// cout << "Second Key: " << Key2 << std::endl;
		// cout << "Third Key: " << Key3 << std::endl;
		;
	}

	void UserTrackPos(int Frame, long Flags, float x, float y, float z) {
		// cout << "Frame Number: " << Frame << std::endl;
		// cout << "Flags: " << Flags << std::endl;
		// cout << "Position - X: " << x << " Y: " << y << " Z: " << z <<
		// std::endl;
		;
	}

	void UserTrackRot(int Frame, long Flags, float DegX, float DegY, float DegZ) {
		// cout << "Frame Number: " << Frame << std::endl;
		// cout << "Flags: " << Flags << std::endl;
		// cout << "Rotation - X: " << DegX << " Y: " << DegY << " Z: " << DegZ
		// << std::endl;
		;
	}

	void UserTrackScale(int Frame, long Flags, float ScaleX, float ScaleY, float ScaleZ) {
		// cout << "Frame Number: " << Frame << std::endl;
		// cout << "Flags: " << Flags << std::endl;
		// cout << "Scaling - X: " << ScaleX << " Y: " << ScaleY << " Z: " <<
		// ScaleZ << std::endl;
		;
	}

	void UserObjNumber(int ObjNumber) {
		// cout << "Object Number: " << ObjNumber << std::endl;
		;
	}

	void UserChunkObj() {
		// cout << std::endl;
		;
	}

	void UserEndOfFile() {
		// cout << "EndOfFile" << std::endl;
		;
	}

	void UserOneUnit(float Unit) {
		// cout << "One Unit is: " << Unit << std::endl;
		;
	}

	void UserViewPortLayout(ViewPortLayout Layout) {
		// cout << "Style: " << Layout.style << std::endl;
		// cout << "Active: " << Layout.active << std::endl;
		// cout << "Unknow1: " << Layout.unknow1 << std::endl;
		// cout << "Swap: " << Layout.swap<< std::endl;
		// cout << "Unknow2: " << Layout.unknow2 << std::endl;
		// cout << "Swap Prior: " << Layout.swap_prior << std::endl;
		// cout << "Swap View: " << Layout.swap_view << std::endl;
		;
	}

	void UserViewPortSize(int PosX, int PosY, int Width, int Height) {
		// cout << "Position X: " << PosX << std::endl;
		// cout << "Position Y: " << PosY << std::endl;
		// cout << "Width: " << PosY << std::endl;
		// cout << "Height: " << PosY << std::endl;
		;
	}

	void UserViewPortData(ViewPortData Data) {
		// cout << "Flags: " << Data.flags << std::endl;
		// cout << "Axis Lockout: " << Data.axis_lockout << std::endl;
		// cout << "Window Position - X: " << Data.win_x << " Y: " << Data.win_y
		// << std::endl;
		// cout << "Window Size - Width: " << Data.win_w << " Height: " <<
		// Data.win_h << std::endl;
		// cout << "Window View: " << Data.win_view << std::endl;
		// cout << "Zoom factor: " << Data.zoom << std::endl;
		// cout << "World Origin - X: " << Data.worldcenter_x << " Y: " <<
		// Data.worldcenter_y << " Z: " << Data.worldcenter_z << std::endl;
		// cout << "Horizontal Angle: " << Data.horiz_ang << std::endl;
		// cout << "Vertical Angle: " << Data.vert_ang << std::endl;
		// cout << "Camera Name: " << Data.camera_name << std::endl;
		;
	}

	void UserViewUser(float x, float y, float z, float Width, float XYangle, float YZangle, float BackAngle) {
		// cout << "Point - X: " << x << " Y: " << y << " Z: " << z <<
		// std::endl;
		// cout << "Width: " << Width << std::endl;
		// cout << "Horizontal view Angle from OXY Plane: " << XYangle <<
		// std::endl;
		// cout << "Vertical view Angle from OYZ Plane: " << YZangle <<
		// std::endl;
		// cout << "BankAngle of View Rotation: " << BackAngle << std::endl;
		;
	}

	void UserViewCamera(String CameraName) {
		// cout << "Default Camera Name: " << CameraName << std::endl;
		;
	}

};


