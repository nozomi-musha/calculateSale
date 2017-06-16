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
	@SuppressWarnings("resource")
	public static void main (String[] args) {

		// 支店ファイル読み込み

		if (args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		HashMap<String ,String> shopmap = new HashMap<String,String> ();
		HashMap<String,Long> shopTotalmap = new HashMap<String,Long> ();

		if (!fileReadmtd(args[0],"branch.lst", "\\d{3}", "支店", shopmap,shopTotalmap)) {
			return;
		}



		//商品定義ファイル読み込み

		HashMap<String,String> menumap = new HashMap<String,String> ();
		HashMap<String,Long> menuTotalmap = new HashMap<String,Long> ();

		if (!fileReadmtd(args[0],"commodity.lst", "^[0-9a-zA-Z]{8}$", "商品", menumap,menuTotalmap)) {
			return;
		}



		//店舗別集計

		BufferedReader br4 = null;

		try {

			File dir = new File(args[0]);
			File[] files = dir.listFiles();
			ArrayList<String> earningsFile = new ArrayList<String>();

			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String filename = file.getName();

				if (filename.matches("\\d{8}.rcd") && files[i].isFile()) {
					earningsFile.add(filename);
				}

			}



			//読み込んだrcdファイルの中で、歯抜け、連番でないものは除外する

			ArrayList<Integer> splitNumber = new ArrayList<Integer>();
			for (int j = 0; j < earningsFile.size(); j++) {
				String rcdSplit = earningsFile.get(j);
				String[] splitItem = rcdSplit.split("\\.");
				Integer splitItem1 = Integer.parseInt(splitItem[0]);
				splitNumber.add(splitItem1);
			}

			for (int k = 0; k < splitNumber.size() - 1; k++) {
				if (splitNumber.get(k + 1) - splitNumber.get(k) != 1) {
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}


			//ArrayLst内のString型ファイルをそれぞれ一行ずつ読み込む

			for (int i = 0 ; i  < earningsFile.size(); i++) {
				ArrayList<String> rcdLine = new ArrayList<String>();
				File file = new File(args[0],earningsFile.get(i));
				FileReader fr = new FileReader(file);
				br4 = new BufferedReader(fr);

				String line = null;

				while ((line = br4.readLine()) != null) {
					rcdLine.add(line);
				}


				//rcdファイルの要素数が3であるか

				if (rcdLine.size() == 3) {
				} else {
					System.out.println(earningsFile.get(i) + "のフォーマットが不正です");
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

				//rcdLineの(2)数字のみであるか

				if (!rcdLine.get(2).matches("^[0-9]*$")) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

				long ln2 = Long.parseLong(rcdLine.get(2));
				long ln3 = shopTotalmap.get(rcdLine.get(0)) + ln2;


				//支店別の合計金額が10桁をこえていないか

				String a = String.valueOf(ln3);

				if (!a.matches("\\d{1,10}")) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				shopTotalmap.put(rcdLine.get(0), ln3);


				//商品別の合計が10桁を超えていないか

				long ln4 = menuTotalmap.get(rcdLine.get(1)) + ln2;
				String b = String.valueOf(ln4);

				if (!b.matches("\\d{1,10}")) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				menuTotalmap.put(rcdLine.get(1), ln4);
			}

		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック;
			System.out.println("予期せぬエラーが発生しました");
			return;

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {
				try {
					if (br4 != null){
						br4.close();
					}
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
		}



		//店舗別集計出力

		if(!fileWritemtd(args[0], "branch.out", shopTotalmap, shopmap)) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}


		//商品別集計出力

		if(!fileWritemtd(args[0], "commodity.out", menuTotalmap, menumap)) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}


	//ファイル出力メソッド

	public static boolean fileWritemtd(String dir, String fileName, HashMap<String,Long> totalMap, HashMap<String ,String> nameMap) {


		List<Entry<String, Long>>
		list_entries = new ArrayList<Entry<String, Long>>(totalMap.entrySet());
		Collections.sort(list_entries, new Comparator<Entry<String, Long>>()
		{


			public int compare(Entry<String, Long> obj1, Entry<String, Long> obj2)
			{
				return obj2.getValue().compareTo(obj1.getValue());
			}
		});



		BufferedWriter bw3 = null;

		try {

			File file = new File(dir,fileName);
			FileWriter fw = new FileWriter(file);
			bw3 = new BufferedWriter(fw);

			for (Entry<String, Long> entry : list_entries) {
				bw3.write(entry.getKey() + "," + nameMap.get(entry.getKey()) + "," + entry.getValue());
				bw3.newLine();
			}

		} catch (FileNotFoundException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("予期せぬエラーが発生しました");
			return false;

		} finally {
			try {
				if (bw3 != null) {
					bw3.close();
				}
			} catch (IOException e) {
				System.out.print("予期せぬエラーが発生しました");
				return false;
			}
		}

		return true;
	}



	public static boolean fileReadmtd(String dir, String fileName, String matchNumber, String printName,
			HashMap<String ,String>mapName, HashMap<String,Long> totalMapname) {


		BufferedReader br = null;

		try {
			File file = new File(dir,fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = null;


			//ファイルの中身を書き出し、,で区切り それぞれにitems[]として保持させる

			while ((line = br.readLine()) != null) {
				String str = line;
				String[] items = str.split(",");


				//ファイルフォーマットの指定

				if (items[0].matches(matchNumber) && items.length == 2)  {
					mapName.put (items[0],items[1]);
					totalMapname.put (items[0],(long) 0);
				} else {
					System.out.println(printName +  "定義ファイルのフォーマットが不正です");
					return false;
				}
			}

		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			System.out.println(printName + "定義ファイルが存在しません");
			return false;

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			System.out.println(printName + "支店定義ファイルが存在しません");
			return false;

		} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
		}

		return true;
	}
}