package excel;

import com.google.common.base.Objects;
import com.google.common.collect.Range;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FreqCapQTest {


	private static final String FILE_NAME = "C:\\Users\\Administrator\\Desktop\\Book1	.xlsx";

	public static void main(String[] args) {

		try {

			FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
			Workbook workbook = new XSSFWorkbook(excelFile);

			Map<UserCamp, Action> prevUserData = null;
			Map<UserCamp, Action> currentUserData = null;

			for (int i = 0; i < 2; i++) {
				Sheet datatypeSheet = workbook.getSheetAt(i);
				Iterator<Row> iterator = datatypeSheet.iterator();
				if (i == 0) {
					prevUserData = getUserData(iterator);
				}
				if(i == 1){
					currentUserData = getUserData(iterator);
				}
			}

			checkResult(prevUserData, currentUserData);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private static void checkResult(Map<UserCamp, Action> prevUserData, Map<UserCamp, Action> currentUserData) {

		System.out.println("RESULT CHECK: \n number of user/campaign = " + prevUserData.size());

		final int[] noUSerData = {0};
		final int[] wrongData = {0};
		final int[] valid = {0};

		List<Action> invalidRequestStat = new ArrayList<>();

		Map<UserCamp, Action> invalidData = prevUserData.entrySet().stream().filter(e -> {
			Action action = currentUserData.get(e.getKey());
			Action prevActions = e.getValue();
			if (currentUserData.get(e.getKey()) == null) {
//				System.out.println("NO user data in current statistic for user = " + e);
				noUSerData[0]++;
				return false;
			} else if(action.getRequests() - prevActions.getRequests() != 0){
//				System.out.println("Inconstancy in user data. prev=" + e + " current=" + currentUserData.get(e.getKey()));
				wrongData[0]++;
				invalidRequestStat.add(new Action(prevActions.getRequests(), action.getRequests()));
				return action.getRequests() - prevActions.getRequests() != 0;
			} else {
				valid[0]++;
				return action.getRequests() - prevActions.getRequests() != 0;
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if(MapUtils.isNotEmpty(invalidData)){
			System.out.println("RESULT CHECK: we have invalid user data. size= " + invalidData.size());
		}

		System.out.println("noUserData = " + noUSerData[0] + " \n wrongData = " + wrongData[0] + "\n valid = " + valid[0]);


		Map<Range, Integer> invalidDataByRange = setupRanges();

		for (Action action : invalidRequestStat) {
			int i = action.getRequests() - action.getImpress();
			for (Map.Entry<Range, Integer> rangeIntegerEntry : invalidDataByRange.entrySet()) {
				if(rangeIntegerEntry.getKey().contains(i)){
					Integer value = rangeIntegerEntry.getValue();
					invalidDataByRange.put(rangeIntegerEntry.getKey(), ++value);
				};
			}
		}

		invalidDataByRange.entrySet()
				.forEach(e -> System.out.println(e.getKey() + " = " + (((double)e.getValue()/wrongData[0]) * 100) + "%"));

		System.out.println(invalidDataByRange);
		System.out.println("FINISH CHECK!!!");

	}

	private static Map<Range, Integer> setupRanges() {
		Map<Range, Integer> result = new LinkedHashMap<>();
		result.put(Range.open(0, 5), 0);
		result.put(Range.open(5, 10), 0);
		result.put(Range.open(10, 20), 0);
		result.put(Range.open(20, 100), 0);
		result.put(Range.open(100, 999999999), 0);
		return result;
	}


	static Map<UserCamp, Action> getUserData(Iterator<Row> iterator){
		Map<UserCamp, Action> result = new HashMap<>();

		while (iterator.hasNext()) {
			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();

			String userId = null;
			String campId = null;
			int req = -1;
			int imps = -1;

			while (cellIterator.hasNext()) {
				Cell currentCell = cellIterator.next();
				int columnIndex = currentCell.getColumnIndex();

				switch (columnIndex){
					case 0:
						userId = currentCell.getStringCellValue();
						break;
					case 1:
						campId = currentCell.getStringCellValue();
						break;
					case 3:
						req = (int) currentCell.getNumericCellValue();
						break;
					case 4:
						imps = (int) currentCell.getNumericCellValue();
						break;
					default:
				}
			}
			UserCamp userCamp = new UserCamp(userId, campId);

			if(result.get(userCamp) != null){
				System.out.println("invalid. same userid and campaignid");
			} else {
				Action action = new Action(imps, req);
				result.put(userCamp, action);
			}
		}

		return result;

	}


	static class Action{
		private final int impress;
		private final int requests;

		Action(int impress, int requests) {
			this.impress = impress;
			this.requests = requests;
		}

		public int getImpress() {
			return impress;
		}

		public int getRequests() {
			return requests;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Action{");
			sb.append("impress=").append(impress);
			sb.append(", requests=").append(requests);
			sb.append('}');
			return sb.toString();
		}
	}

	static class UserCamp{
		private final String userId;
		private final String campId;

		public UserCamp(String userId, String campId) {
			this.userId = userId;
			this.campId = campId;
		}

		public String getUserId() {
			return userId;
		}

		public String getCampId() {
			return campId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			UserCamp userCamp = (UserCamp) o;
			return Objects.equal(userId, userCamp.userId) &&
					Objects.equal(campId, userCamp.campId);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(userId, campId);
		}


	}


}

