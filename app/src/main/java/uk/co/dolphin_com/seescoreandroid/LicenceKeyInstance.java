/**
 * SeeScore For Android
 * Dolphin Computing http://www.dolphin-com.co.uk
 */
/* SeeScoreLib Key for Notefy

 IMPORTANT! This file is for Notefy only.
 It must be used only for the application for which it is licensed,
 and must not be released to any other individual or company.

 Please keep it safe, and make sure you don't post it online or email it.
 Keep it in a separate folder from your source code, so that when you backup the code
 or store it in a source management system, the key is not included.
 */

package uk.co.dolphin_com.seescoreandroid;

import uk.co.dolphin_com.sscore.SScoreKey;

/**
 * The licence key to enable features in SeeScoreLib supplied by Dolphin Computing
 */

public class LicenceKeyInstance
{
// licence keys: draw, android, embed_id
	private static final int[] keycap = {0X84001,0X0};
	private static final int[] keycode = {0X7eb0cf8c,0Xa0a314f,0X40930327,0X232b3eaf,0X6c206532,0Xb47b44c0,0X42d3ed13,0X67719cab,0X6c70af45,0X53e4d39b,0X54009024,0X43afeafb,0X70f75691,0X7e548387,0X29b22aa9};

	public static final SScoreKey SeeScoreLibKey = new SScoreKey("Notefy", keycap, keycode);
}
