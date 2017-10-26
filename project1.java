package suchi_project1;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.BufferedWriter;

public class project1 {
	private HashMap<String, Integer> itemdict;
	private int val = 0;
	static int support;
	int temp;
	static int k_value;
	int count = 0;
	int index=1;
	String inputFileName;
	String outputFileName;
	File output_file;
	List<List<Integer>> dataRecords = new ArrayList<List<Integer>>();
	public static void main(String[] args) {
		long starttime = System.nanoTime();
		try {
			//Users//suchivedala//Desktop//Spring 2017//Data Mining//suchi_project//project1//transactionDB.txt
			//Users//suchivedala//Desktop//Spring 2017//Data Mining//suchi_project//project1//ouput(s=4,k=4).txt
			trienode t1 = new trienode(0);
			project1 p1 = new project1();
			int sup=Integer.parseInt(args[0]);
			int k = Integer.parseInt(args[1]);
			String infile = args[2];
			String ofile = args[3];
			p1.inputFileName = infile;
			p1.outputFileName = ofile;
			p1.support = sup;
			p1.output_file = new File(ofile);
			p1.count++;
			p1.k_value=k;
			long time1= System.nanoTime();
			p1.dataRecords = p1.dataProcessor(infile, k);
			long l1 = checktime(time1,starttime);
			long time2 = System.nanoTime();
			trienode objTrienode1 = p1.initialCandidate(infile, k, t1);
			long l2 = checktime(time2,starttime);
			long time3 = System.nanoTime();
			while (!objTrienode1.child.isEmpty())
			{
				if (p1.count >= k)
				{
					long time4 = System.nanoTime();
					p1.writeToFile(ofile, objTrienode1, " ");
					long l4 =checktime(time4,starttime);
				}
				p1.count++;
				long time5 = System.nanoTime();
				p1.candidates(objTrienode1, 0);
				long l5 = checktime(time5,starttime);
				long time6 = System.nanoTime();
				p1.trienodeCounter(objTrienode1);
				long l6 =checktime(time6,starttime);
				long time7 = System.nanoTime();
				p1.removeNode(objTrienode1, 1);
				long l7 =checktime(time7,starttime);
			}
			long l3 = checktime(time3, starttime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Process completed. Itemsets with support count are written to file ");
		long endtime = System.nanoTime();
		long duration = checktime(endtime,starttime);
		System.out.println("Time taken to generate frequent itemsets for k=" +k_value+ " and sup=" +support+ " is " +duration+" sec");
	}
	
	public static long checktime(long t1,long st)
	{
		long current_time = System.nanoTime();
		long duration = (current_time-t1)/1000000000;
		long totaltime = (current_time-st)/1000000000;
		if(totaltime>420)
		{
			System.out.println("Code is slow with these inputs ");
		}
		return totaltime;
	}
	
	
	
	
	public int check(String filename)
	{
		int flag;
		File file = new File(filename);
		if(file.isFile() && file.exists())
		{
			flag=1;
		}
		else
		{
			flag=0;
		}
		return flag;
	}
	public List<List<Integer>> dataProcessor(String filename, int k)
	{
		List<List<Integer>> objList = new ArrayList<List<Integer>>();
		itemdict = new HashMap<>();
		try 
		{
			//File file = new File(filename);
			int check = check(filename);
			if (check==1) {
				FileReader fr = new FileReader(filename);
				BufferedReader br = new BufferedReader(fr);
				String lines = null;
				while ((lines = br.readLine()) != null) 
				{
					String[] lineno = lines.split(" ");
					if (lineno.length >= k)
					{
						List<Integer> num_lines = new ArrayList<Integer>();
						int i = 0; 
						while(i < lineno.length) 
						{
							if (itemdict.containsKey(lineno[i]))
								num_lines.add(itemdict.get(lineno[i]));
							else 
							{
								
								num_lines.add(val);
								val++;
								itemdict.put(lineno[i], val);
							}
							i++;
						}
						Collections.sort(num_lines);
						objList.add(num_lines);
					}
				}
				fr.close();
			}
		} catch (Exception e) 
		{
			System.out.println("Input data error");
			e.printStackTrace();
		}
		return objList;
	}

	public trienode initialCandidate(String filename, int k, trienode root) 
	{	
		int temp;
		for (int i = 0; i < val; i++) 
		{	
			temp=i+1;
			root.insert(temp);
		}
		for (int j = 0; j < dataRecords.size(); j++)
		{
			List<Integer> num_lines = new ArrayList<Integer>();
			num_lines = dataRecords.get(j);
			counter(root, num_lines, 0);
		}
		removeNode(root, 1);
		return root;
	}
	
	public void candidates(trienode root, int k) 
	{
		int level1 = count - 2;
		if (level1 == k) 
		{
			for (int i = 0; i < root.child.size() - 1; i++)
			{
				for (int j = i+1; j < root.child.size(); j++)
				{
					trienode next = root.child.get(i);
					next.child.add(new trienode(root.child.get(j).getnode()));
				}
			}
			root.child.remove(root.child.size() - 1);
		} 
		else if (level1 > k)
		{
			for (trienode n:root.child) 
			{
				candidates(n,k + 1);
			}
		}
	}

	public void removeNode(trienode root, int l)
	{
		ListIterator<trienode> li = root.child.listIterator();
		while (li.hasNext()) 
		{
			trienode node = li.next();
			if (!node.child.isEmpty()) 
			{
				removeNode(node, l + 1);
			}
			if ((node.child.isEmpty() && node.num < support))
			{
				li.remove();
			}
			if (node.child.isEmpty() && l < count)
				li.remove();
		}
	}

	public void writeToFile(String outfile, trienode root, String data) 
	{
		if (root.val != 0)
		{
			for (String key : itemdict.keySet()) 
			{
				if (itemdict.get(key).equals(root.val))
					data = data + key + " ";
			}
		}
		if (root.child.isEmpty()) 
		{	
			temp = index++;
			write( temp, outfile, root,data);
			
		} 
		else 
		{
			for (trienode s : root.child) {
				writeToFile(outfile, s, data);
			}
		}
	}
	public void write(int i, String outfile, trienode root,String data)
	{
		try 
		{
			FileWriter fr = new FileWriter(outfile, true);
			BufferedWriter br = new BufferedWriter(fr);
			br.write(data + "(" + root.num + " )\n");
			//br.write("----------------------------------------\n");
			br.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Writing to File has caused some problem. Please see if path is correct ");
			e.printStackTrace();
		}
	}

	public void trienodeCounter(trienode root) 
	{
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < dataRecords.size(); i++) 
		{
			l = dataRecords.get(i);
			counter(root, l, 0);
		}
	
	}
	public void counter(trienode root, List<Integer> l, int k)
	{
		if (root.child.isEmpty())
			root.num++;
		else 
		{
			for (int i = k; i < l.size(); i++)
			{
				trienode child = root.search(l.get(i));
				if (child != null) 
				{
					counter(child, l, k + 1);
				}
			}
		}
	}
}


