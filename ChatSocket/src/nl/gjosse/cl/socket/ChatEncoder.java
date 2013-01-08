package nl.gjosse.cl.socket;

public class ChatEncoder {

	public static String encodeString(String text) {
		StringBuilder builder = new StringBuilder();
		
		for(int i=0;i<text.length();i++)
		{
		char letter = text.charAt(i);
		int unicode = (int)letter;
		unicode = unicode * 3;
		unicode = unicode +5;
		String done = "" +unicode;
		builder.append(done);
		}
		
		return builder.toString();	
	}
	
	
	public static String decodeString(String text)
	{
		StringBuilder builder = new StringBuilder();
		int counter = 0;
		int numb1 = 0;
		int numb2 = 0;
		
		for(int i=0;i<text.length();i++)
		{
			if(Character.isDigit(text.charAt(i)))
			{
			counter++;
			
			if(counter==1)
			{
				String numb = ""+text.charAt(i);
				numb1 = Integer.parseInt(numb);
			}
			if(counter==2)
			{
				String numb = ""+text.charAt(i);
				numb2 = Integer.parseInt(numb);
			}
			if(counter==3)
			{
				String numb = ""+text.charAt(i);
				int numb3 = Integer.parseInt(numb);
				String onechar = ""+numb1 + numb2 +numb3;
				int unicode = Integer.parseInt(onechar);
				unicode = unicode - 5;
				unicode = unicode /3;
				char done = (char) unicode;
				builder.append(done);
				counter = 0;
			}
		 }
			else
			{
				return text;
			}
		

		}
		return builder.toString();
	}

}
