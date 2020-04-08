/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lsfr;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Elliot
 */
public class LSFR {
    
    public ArrayList<String> readfile(String path) throws IOException {
        File file = new File(path);
        ArrayList<String> temp = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            temp.add(st);
        }
        return temp;
    }
    public String arrayToString(ArrayList<String> array){
        StringBuffer sb = new StringBuffer();

        for (String s : array) {
           sb.append(s);
        }
        String str = sb.toString();
        return str;
    }
    public String self_XORing(String XOR[]){
        String output;
        int value;
        String tmp1 = XOR[0];
        String tmp2 = XOR[1];

        if((tmp1.equals("1") && tmp2.equals("0"))  || (tmp1.equals("0") && tmp2.equals("1")))
            value = 1;
        else 
            value = 0;
        for(int i = 2 ; i <= XOR.length -1; i++){
            String tmp = XOR[i];
            if((tmp.equals("1") && value == 0)  || (tmp.equals("0") && value == 1))
                value = 1;
            else 
                value = 0;
        }
        output = Integer.toString(value);
        return output;
    }
    public String XORing(String plainText, String key){
        ArrayList<String> output = new ArrayList<String>();
        String[] plainChars = plainText.split("");
        String[] keyChars = key.split("");
        for(int i = 0 ; i < key.length() ; i++){
            if((plainChars[i].equals("1") && keyChars[i].equals("0"))  || (plainChars[i].equals("0") && keyChars[i].equals("1")))
                output.add("1");
            
            else
                output.add("0");
        }
        String tmp = arrayToString(output);
        
        return tmp;
    }
    public String generateKey(String initial, String pol, int warmUp, int plainSize){
        String[] regesters = initial.split("");
        String[] pols = pol.split("");
        int size = regesters.length;
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        ArrayList<String> output = new ArrayList<String>();
        
        for(int i = 0 ; i < pols.length ; i++){//know which regesters to XOR
            if(pols[i].equals("1"))
                indexes.add(i);
        }
        
        for(int j = 0 ; j < warmUp + plainSize; j++){
            output.add(regesters[0]);
            String tmp = regesters[size-1];//store the value of S9

            for(int i = 0 ; i < regesters.length-2 ; i++){//move step
                regesters[i] = regesters[i+1];
            }
            regesters[size-2] = tmp;//S8 = old S9
            
            String[] XOR = new String[indexes.size()];
            for(int i = 0 ; i < indexes.size() ; i++){
                XOR[i] = regesters[indexes.get(i)];//get the current value of the regester and store it
            }
            regesters[size-1] = self_XORing(XOR);
            
        }
        System.out.print("discarding bits : ");
        for(int i = 0 ; i< warmUp ; i++){
            System.out.print(output.get(i) + " ");
        }
        System.out.println("");
        String tmp = arrayToString(output);
        tmp = tmp.substring(warmUp, tmp.length());//removing the warmUps bits
        
        return tmp;
    }
    public String cypher(String plainText) throws IOException{
        String path = "F:\\college\\third year\\second term\\Computer Network Security\\assignments\\LSFR_Encrypter\\LSFR\\data.txt";
        ArrayList<String> tmp = new ArrayList<String>();
        ArrayList<String> data = readfile(path);
        String key = generateKey(data.get(0), data.get(1), Integer.parseInt(data.get(2)), (plainText.length())*8);
        System.out.println("key : "+key);
        for(int i = 0 ; i<plainText.length() ; i++){
            int num = (int)plainText.charAt(i);//get ascii
            String binary = Integer.toBinaryString(num); //get binary  0 1110101
            while(binary.length() < 8){//add the missing bits
                binary = "0" + binary;
            }
            tmp.add(binary);
        }
        String plainBinary = arrayToString(tmp);
        String ciphered = XORing(plainBinary, key);
        
        FileWriter fw=new FileWriter("F:\\college\\third year\\second term\\Computer Network Security\\assignments\\LSFR_Encrypter\\LSFR\\ciphered.txt");
        for (int i = 0; i < ciphered.length(); i++) 
            fw.write(ciphered.charAt(i));
        fw.close();
        
        return ciphered;
    }
    public String decipher() throws IOException{
        String path = "F:\\college\\third year\\second term\\Computer Network Security\\assignments\\LSFR_Encrypter\\LSFR\\data.txt";
        String path2 = "F:\\college\\third year\\second term\\Computer Network Security\\assignments\\LSFR_Encrypter\\LSFR\\ciphered.txt";
        
        ArrayList<String> data = readfile(path);
        ArrayList<String> cipheredData = readfile(path2);
        
        String ciphered = cipheredData.get(0);
        ArrayList<String> charsBinary = new ArrayList<String>();
        ArrayList<Character> chars = new ArrayList<Character>();
        String key = generateKey(data.get(0), data.get(1), Integer.parseInt(data.get(2)), cipheredData.get(0).length());
        
        String originalBinary = XORing(ciphered, key);
        
        for(int i = 0 ; i < originalBinary.length() ; i+=8){
            charsBinary.add(originalBinary.substring(i,i+8));
        }
        for(int i = 0 ; i < charsBinary.size() ;i++){
            int tmp = Integer.parseInt(charsBinary.get(i),2);//get ascii
            chars.add((char)tmp);//get char
        }
        
        String originalText = "";
        for(int i = 0 ; i <chars.size() ; i++){
            originalText += chars.get(i);
        }
        return originalText;
    }
    public static void main(String[] args) throws IOException {
        LSFR lsfr = new LSFR();
        String plainText = "mohamed ashraf";
        String ciphered = lsfr.cypher(plainText);
        String original = lsfr.decipher();
        
        System.out.println("ciphered bits : " + ciphered);
        System.out.println("origianl : " + original);
    }
    
}
