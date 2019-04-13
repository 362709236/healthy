package cn.cch.healthy.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    @Test
    public static  List<List<Integer>> getAllCombinerDun(List<Integer> data){
        List<List<Integer>>  combinerResults = new ArrayList<>();

        combinerSelect(combinerResults, data, new ArrayList<Integer>(), data.size(), 3);

        System.out.println(combinerResults.size());
//        logger.info("组合结果：{}", results.toString());
        return combinerResults;
    }


    /***
     * C(n,k) 从n个中找出k个组合
     * @param data
     * @param workSpace
     * @param n
     * @param k
     * @return
     */
    public static List<List<Integer>>  combinerSelect(List<List<Integer>> combinerResults, List<Integer> data, List<Integer> workSpace, int n, int k) {
        List<Integer> copyData;
        List<Integer> copyWorkSpace;

        if(workSpace.size() == k) {
            List<Integer>  dunTiles = new ArrayList<>();
            for(Integer c : workSpace){
                dunTiles.add(c);
            }

            combinerResults.add(dunTiles);
        }

        for(int i = 0; i < data.size(); i++) {
            copyData = new ArrayList<>(data);
            copyWorkSpace = new ArrayList<>(workSpace);

            copyWorkSpace.add(copyData.get(i));
            for(int j = i; j >=  0; j--)
                copyData.remove(j);
            combinerSelect(combinerResults, copyData, copyWorkSpace, n, k);
        }


        return  combinerResults;
    }

}
