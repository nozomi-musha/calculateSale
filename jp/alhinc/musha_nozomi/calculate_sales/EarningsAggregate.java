package jp.alhinc.musha_nozomi.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

class EarningsAggregate {
	public static void main (String[] args) {

		// 支店ファイル読み込み

		HashMap<String ,String> shopmap = new HashMap<String,String> ();
		HashMap<String,Long> shopTotalmap = new HashMap<String,Long> ();
		BufferedReader br = null;

		try {
			File file = new File(args[0],"branch.lst");
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = null;

			//ファイルの中身を書き出し、,で区切る
			//それぞれにitems[]として保持させる

			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String str = line;
				String[] items = str.split(",");

				//ファイルフォーマットの指定

				if (items[0].matches("\\d{3}") && items.length == 2) {
					shopmap.put (items[0],items[1]);
					shopTotalmap.put (items[0],(long) 0);
				} else {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					("")
				}
		}



		//商品定義ファイル読み込み

		HashMap<String,String> menumap = new HashMap<String,String> ();
		HashMap<String,Long> menuTotalmap = new HashMap<String,Long> ();
		BufferedReader br1 = null;

		try {
			File file = new File(args[0],"commodity.lst");
			FileReader fr = new FileReader(file);
			br1 = new BufferedReader(fr);
			String line = null;

			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String str = line;
				String[] items = str.split(",");

			//ファイルフォーマットの指定

				String str1 = items[0];

				if (str1.matches("^[0-9 A-Z]{8}$") && items.length == 2)  {
					menumap.put (items[0],items[1]);
					menuTotalmap.put (items[0],(long) 0);

				} else {
					System.out.println("商品店定義ファイルのフォーマットが不正です");
					return;
				}
			}
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("商品定義ファイルが見つかりませんでした");
				}
		}


		//店舗別集計

		try {

			File dir = new File(args[0]);
			File[] files = dir.listFiles();
			ArrayList<String> earningsFile = new ArrayList<String>();

			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String filename = file.getName();

				if (filename.matches("\\d{8}.rcd")) {
					earningsFile.add(filename);
				}

			}


			//読み込んだrcdファイルの中で、歯抜け、連番でないものは除外する
			ArrayList<Integer> splitNumber = new ArrayList<Integer>();
			for (int j = 0; j < earningsFile.size(); j++) {
				String rcdSplit = earningsFile.get(j);
				String[] splitItem = rcdSplit.split("\\.");
				String splitItem1 = splitItem[0];
				int splitNumber1 = Integer.parseInt(splitItem1);

				splitNumber.add(splitNumber1);
			}

			for (int k = 0; k < splitNumber.size() - 1; k++) {
				if (splitNumber.get(k + 1) - splitNumber.get(k) != 1) {
					System.out.println("売上ファイルが連番になっていません");
					return;
				}
			}

		//ArrayLst内のString型ファイルをそれぞれ一行ずつ読み込む

			for (int i = 0 ; i  < earningsFile.size(); i++) {
				ArrayList<String> rcdLine = new ArrayList<String>();
				File file = new File(args[0],earningsFile.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br2 = new BufferedReader(fr);

				String line ;

				while ((line = br.readLine()) != null) {
					rcdLine.add(line);
				}

		//rcdファイルの要素数が3であるか

				if (rcdLine.size() == 3) {
				} else {
					System.out.println(earningsFile.get(i) + "のフォーマットが不正です");
					return;
				}
				//rcdLineの(2)が10桁をこえていないか
				if (!rcdLine.get(2).matches("\\d{1,9}")) {
					System.out.println(earningsFile.get(i) + "の合計金額が10桁を超えています");
					return;
				}

		//ファイルの支店コードが正しいか(コンテンツキー)
				if (!shopmap.containsKey(rcdLine.get(0))) {
					System.out.println(earningsFile.get(i) + "の支店コードが不正です");
					return;
				}

				//ファイルの商品コードが正しいか(コンテンツキー)
				if (!menumap.containsKey(rcdLine.get(1))) {
					System.out.println(earningsFile.get(i)  + "の商品コードが不正です");
					return;
				}

				long ln2 = Long.parseLong(rcdLine.get(2));
				long ln3 = shopTotalmap.get(rcdLine.get(0)) + ln2;
				shopTotalmap.put(rcdLine.get(0), ln3);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("エラー");
			return;
		}

		//shopTotalmapのvalueを降順に

		List<Entry<String, Long>>
		list_entries = new ArrayList<Entry<String, Long>>(shopTotalmap.entrySet());
		Collections.sort(list_entries, new Comparator<Entry<String, Long>>()
		{
			public int compare(Entry<String, Long> obj1, Entry<String, Long> obj2)
			{
				return obj2.getValue().compareTo(obj1.getValue());
			}
		});
		for (Entry<String, Long> entry : list_entries) {
		}


		//店舗別集計出力

		try {
			File file = new File(args[0],"branch.out");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			for (Entry<String, Long> entry : list_entries) {
				bw.write(entry.getKey() + " , " + shopmap.get(entry.getKey()) + " , " + entry.getValue() + "\n");
			}
			bw.close();
		} catch (IOException e)  {
			System.out.println("予期せぬエラーが発生しました");
		}


		//商品別集計

		try {

			File dir2 = new File(args[0]);
			File[] files2 = dir2.listFiles();
			ArrayList<String> earningsFile2 = new ArrayList<String>();

			for (int i = 0; i < files2.length; i++) {
				File file = files2[i];
				String filename = file.getName();

				if (filename.matches("\\d{8}.rcd")) {
					earningsFile2.add(filename);
				}
			}

		//ArrayLst内のString型ファイルをそれぞれ一行ずつ読み込む

			for (int i = 0 ; i  < earningsFile2.size(); i++) {
				ArrayList<String> rcdLine2 = new ArrayList<String>();
				File file2 = new File(args[0],earningsFile2.get(i));
				FileReader fr2 = new FileReader(file2);
				BufferedReader br2 = new BufferedReader(fr2);

				String line2 ;

				while ((line2 = br2.readLine()) != null) {
					rcdLine2.add(line2);
				}

				long ln4 = Long.parseLong(rcdLine2.get(2));
				long ln5 = menuTotalmap.get(rcdLine2.get(1)) + ln4;
				menuTotalmap.put(rcdLine2.get(1), ln5);

				br2.close();
				}
			} catch (IOException e) {
			e.printStackTrace();
			System.out.println("エラー");
			return;
		}

		//menuTotalmapのvalueを降順に

		List<Entry<String, Long>>
		list_entries2 = new ArrayList<Entry<String, Long>>(menuTotalmap.entrySet());
		Collections.sort(list_entries2, new Comparator<Entry<String, Long>>()
		{
			public int compare(Entry<String, Long> obj1, Entry<String, Long> obj2)
			{
				return obj2.getValue().compareTo(obj1.getValue());
			}
		});
		for (Entry<String, Long> entry : list_entries2) {
		}

		//商品別集計出力

		try {
			File file = new File(args[0],"commodity.out");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			for (Entry<String, Long> entry : list_entries2) {
				bw.write(entry.getKey() + " , " + menumap.get(entry.getKey()) + " , " + entry.getValue() + "\n");
			}
			bw.close();
		} catch (IOException e)  {
			System.out.println("予期せぬエラーが発生しました");

		}

	}
}
