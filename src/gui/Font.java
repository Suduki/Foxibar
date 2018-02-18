package gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import display.Texture;

public class Font {
	public class CharacterDefinition {
		public float u0=0, v0=0, u1=0, v1=0;		
	}
	
	private CharacterDefinition[] mCharDefs = null;
	private Texture mTexture = null;
	private Pattern mDelimiterPattern = null;
	private float mScaleU = 0;
	private float mScaleV = 0;

	private static Map<String,Font> mFonts = new TreeMap<>();
	
	public static Font defaultFont() {
		return getFont("Consolas");
	}
	
	public static Font getFont(String pFontName) {
		if (!mFonts.containsKey(pFontName)) {
			mFonts.put(pFontName, new Font(pFontName));
		}
		
		return mFonts.get(pFontName);
	}
	
	public Texture getTexture() {
		return mTexture;
	}
	
	private Font(String pFontName) {		
		String textureFilename = "pics/" + pFontName + ".png";
		String metaFilename    = "pics/" + pFontName + ".fnt";
		mCharDefs = new CharacterDefinition[256];
		mTexture = Texture.fromFile(textureFilename);
		mDelimiterPattern = Pattern.compile("[=\\s]+");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(metaFilename));
			
			String line;
			while ((line = br.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				String what = scanner.next();
				
				switch (what) {
				case "common":
					readCommonData(scanner);
					
				case "char":
					readCharDefinition(scanner);
					break;
				}
			}
			
			br.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("Font file \"" + metaFilename + "\" could not be opened: " + e.getMessage());
		}
		catch (IOException e) {
			System.err.println("Failed to read from \"" + metaFilename + "\": " + e.getMessage());
		}
	}
	
	public CharacterDefinition getCharacterDefinition(int pIndex) {
		if (pIndex >= 0 && pIndex < mCharDefs.length) {
			return mCharDefs[pIndex];
		}
		
		return null;
	}
	
	private void readCommonData(Scanner scanner) {
		int lineHeight=0, base=0, scaleW=1, scaleH=1, pages=0, packed=0;
		scanner.useDelimiter(mDelimiterPattern);
		while (scanner.hasNext()) {
			String token = scanner.next();
			switch(token) {
			case "lineHeight":
				lineHeight = scanner.nextInt();
				break;
			case "base":
				base = scanner.nextInt();
				break;
			case "scaleW":
				scaleW = scanner.nextInt();
				System.out.println("scaleW = " + scaleW);
				break;
			case "scaleH":
				scaleH = scanner.nextInt();
				System.out.println("scaleH = " + scaleH);
				break;
			case "pages":
				pages = scanner.nextInt();
				break;
			case "packed":
				packed = scanner.nextInt();
				break;
			default:
				break;
			}
		}
		
		mScaleU = 1.0f/scaleW;
		mScaleV = 1.0f/scaleH;
	}
	
	private void readCharDefinition(Scanner scanner) {
		int id=0, x=0, y=0, width=0, height=0, xoffset=0, yoffset=0, xadvance=0, page=0, chnl=0;
		scanner.useDelimiter(mDelimiterPattern);
		while (scanner.hasNext()) {
			String token = scanner.next();
			switch(token) {
			case "id":
				id = scanner.nextInt();
				break;
			case "x":
				x = scanner.nextInt();
				break;
			case "y":
				y = scanner.nextInt();
				break;
			case "width":
				width = scanner.nextInt();
				break;
			case "height":
				height = scanner.nextInt();
				break;
			case "xoffset":
				xoffset = scanner.nextInt();
				break;
			case "yoffset":
				yoffset = scanner.nextInt();
				break;
			case "xadvance":
				xadvance = scanner.nextInt();
				break;
			case "page":
				page = scanner.nextInt();
				break;
			case "chnl":
				chnl = scanner.nextInt();
				break;
			default:
				break;
				
			}
		}
		
		CharacterDefinition def = new CharacterDefinition(); 
		def.u0 = x*mScaleU;
		def.v0 = y*mScaleV;
		def.u1 = (x+width)*mScaleU;
		def.v1 = (y+height)*mScaleV;
		mCharDefs[id] = def;
	}
}
