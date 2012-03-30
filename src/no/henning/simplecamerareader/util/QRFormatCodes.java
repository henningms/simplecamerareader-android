package no.henning.simplecamerareader.util;

import java.util.Collection;
import java.util.EnumSet;

import com.google.zxing.BarcodeFormat;

public class QRFormatCodes
{
	public static final Collection<BarcodeFormat> PRODUCT_FORMATS;
	public static final Collection<BarcodeFormat> ONE_D_FORMATS;
	public static final Collection<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
	public static final Collection<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(BarcodeFormat.DATA_MATRIX);
	public static final Collection<BarcodeFormat> ALL_FORMATS;
	
	static {
		PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,                
				BarcodeFormat.UPC_E,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.RSS_14);
		
		ONE_D_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.ITF,
                BarcodeFormat.CODABAR);
		
		ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
		
		ALL_FORMATS = EnumSet.of(
				BarcodeFormat.AZTEC,
				BarcodeFormat.CODABAR,
				BarcodeFormat.CODE_128,
				BarcodeFormat.CODE_39,
				BarcodeFormat.CODE_93,
				BarcodeFormat.DATA_MATRIX,
				BarcodeFormat.EAN_13,
				BarcodeFormat.EAN_8,
				BarcodeFormat.ITF,
				BarcodeFormat.MAXICODE,
				BarcodeFormat.PDF_417,
				BarcodeFormat.QR_CODE,
				BarcodeFormat.RSS_14,
				BarcodeFormat.RSS_EXPANDED,
				BarcodeFormat.UPC_A,
				BarcodeFormat.UPC_E,
				BarcodeFormat.UPC_EAN_EXTENSION);
  }
}
