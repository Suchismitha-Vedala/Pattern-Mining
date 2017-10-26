package suchi_project1;
import java.util.LinkedList;

public class trienode{
		 int num;
		 LinkedList<trienode> child;
		 int val;
		trienode(int item){
			this.val=item;
			num=0;
			child=new LinkedList<trienode>();
		}
	public void insert(int item){
		trienode node =new trienode(item);
		node.val=item;
		this.child.add(node);
	}
	public trienode search (int item){
		for(trienode s: this.child){
			if(s.val==item)
				return s;
		}
		return null;
	}
	public int getnode(){
		return val;
	}

}