package com.songkick.snippets.util;

import com.petebevin.markdown.MarkdownProcessor;

public class TestMarkdown {
	private static String testEmail = "# Last week" +
			"<br>" +
			"<br>  * 2 back-end dev coding exercises" +
			"<br>  * Worked our way back to where we were with the OneKicker and signup flows" +
			"<br>back before THE EVENT" +
			"<br>    - Started turning the flows spike into a rails plugin" +
			"<br>    - Wrote down some guiding design principles (Readme-driven development)," +
			"<br>including some thinking around how this plugin will be different from" +
			"<br>action_flow (mainly: simpler, less general-purpose, more prescriptive)" +
			"<br>  * Hacked in some Markdown support for snippets (yay)" +
			"<br>" +
			"<br># This week" +
			"<br>" +
			"<br>  * Continue the transformation of our spike" +
			"<br>  * Starting work on an actual flow in skweb-user that employs the plugin" +
			"<br>  * Something something on a boat" +
			"<br>";
	
		public static void main(String[] args) {
			MarkdownProcessor markdown = new MarkdownProcessor();
			
			String marked = markdown.markdown(testEmail.replaceAll("<br>", "\n"));
			
			System.out.println(marked);
		}
	
}
