/**
 * 
 */
package com.bottinifuel.pladd.EFT;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 *
 */
public class BankLookup {

	static private final Object InitLock = new Object();
	static private final Map<BigInteger, String> Banks;

	static private final Boolean Testing = false;
	static private final String TestFile = "\\\\bottini001\\wappingers\\data\\Patrick\\EFT\\All ABAs.txt";
	static private final String TestSerial = "C:\\Documents and Settings\\Administrator\\Desktop\\All ABAs.jso";

	static {
		Map<BigInteger, String> temp = null;
		
		synchronized(InitLock)
		{
			// First - read serialized hash map from previous session
			try {
				ObjectInputStream is;
				if (Testing)
					is = new ObjectInputStream(new FileInputStream(TestSerial));
				else
					is = new ObjectInputStream(BankLookup.class.getResourceAsStream("/aba.jso"));
				temp = (Map<BigInteger, String>)is.readObject();
				System.out.println("Loading serialized objects succeeded");
			}
			catch (Exception ex)
			{
				System.out.println("Loading serialized objects failed");
				// Serialization restore failed - read from raw data file
				try {
					temp = new HashMap<BigInteger, String>();
					InputStream is;
					if (Testing)
						is = new FileInputStream(TestFile);
					else
						is = BankLookup.class.getResourceAsStream("/aba.txt");
					BufferedReader f = new BufferedReader(new InputStreamReader(is));
					Pattern p = Pattern.compile("(\\d{9})\t(.*)\t(.*)\t(.*)\t.*");

					String s = f.readLine();
					while (s != null)
					{
						Matcher m = p.matcher(s);
						if (m.matches())
						{
							BigInteger ABA = new BigInteger(m.group(1));
							String name = m.group(2).trim();
							String city = m.group(3).trim();
							String state = m.group(4).trim();
							String bank = name + "\n" + city + ", " + state;
							temp.put(ABA, bank);
						}
						s = f.readLine();
					}

					ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(TestSerial));
					os.writeObject(temp);
				}
				catch (Exception e)
				{
				}
			}
			Banks = temp;
			System.out.println(Banks.size() + " objects in Banks");
		}
	}

	static public String BankName(EFTRoutingNumber rn)
	{
		if (rn == null)
			return null;
		synchronized(InitLock)
		{
			if (Banks.containsKey(rn.GetNumber()))
				return (String)Banks.get(rn.GetNumber());
			else
				return null;
		}
	}
}
