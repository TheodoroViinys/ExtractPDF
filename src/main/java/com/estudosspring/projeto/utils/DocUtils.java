package com.estudosspring.projeto.utils;

import com.estudosspring.projeto.enums.DOC_TYPE;

public class DocUtils {

	public static DOC_TYPE verifyTypeDoc(byte[] body) {

		if (body[0] == (byte) 0x25 && body[1] == (byte) 0x50 && body[2] == (byte) 0x44 && body[3] == (byte) 0x46) {
			return DOC_TYPE.PDF;

		} else if (body[0] == (byte) 0x50 && body[1] == (byte) 0x4B && body[2] == (byte) 0x03 && body[3] == (byte) 0x04) {
			return DOC_TYPE.DOCX;

		} else if (body[0] == (byte) 0xD0 && body[1] == (byte) 0xCF && body[2] == (byte) 0x11 && body[3] == (byte) 0xE0 && body[4] == (byte) 0xA1 && body[5] == (byte) 0xB1 && body[6] == (byte) 0x1A && body[7] == (byte) 0xE1) {
			return DOC_TYPE.DOC;

		} else if (new String(body).contains("PNG")) {
			return DOC_TYPE.PNG;

		} else if (new String(body).contains("<!DOCTYPE html>")) {
			return DOC_TYPE.HTML;

		} else {
			return DOC_TYPE.DEFAULT;
		}
	}
}
