package edu.buffalo.cse.irf14.indexsearcher;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyList implements Comparator<Map.Entry<String,Double>>{
	List<Map.Entry<String,Double>> list; 
	
	public MyList(List<Map.Entry<String,Double>> paramList){
		this.list = paramList;
	}
	
	public int compare(Map.Entry<String,Double> key1,
			Map.Entry<String,Double> key2){
		Map.Entry<String,Double> e1;
		Double v1=null;
		Double v2=null;
		
		Iterator<Map.Entry<String,Double>> itr = this.list.iterator();
		
		while(itr.hasNext()){
			e1 = itr.next();
			if(e1.getKey().equals(key1.getKey()))
				v1 = e1.getValue();
		}
		
        Iterator<Map.Entry<String,Double>> itr1 = this.list.iterator();
		
		while(itr1.hasNext()){
			e1 = itr1.next();
			if(e1.getKey().equals(key2.getKey()))
				v2 = e1.getValue();
		}
		
		if(v1.compareTo(v2)>0)
			return -1;
		else 
			return 1;
	}
}
