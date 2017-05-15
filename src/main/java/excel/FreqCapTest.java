package excel;

import com.google.common.base.Objects;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FreqCapTest {

	private static final String FILE_NAME = "C:\\Users\\Administrator\\Desktop\\fr3.xlsx";

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

		System.out.println("FINISH CHECK!!!");

	}


	static Map<UserCamp, Action> getUserData(Iterator<Row> iterator){
		Map<UserCamp, Action> result = new HashMap<>();

		while (iterator.hasNext()) {
			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();

			String userId = null;
			int campId = 0;
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
						campId = (int) currentCell.getNumericCellValue();
						break;
					case 2:
						req = (int) currentCell.getNumericCellValue();
						break;
					case 3:
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

}

class Action{
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

class UserCamp{
	private final String userId;
	private final int campId;

	UserCamp(String userId, int campId) {
		this.userId = userId;
		this.campId = campId;
	}

	public String getUserId() {
		return userId;
	}

	public int getCampId() {
		return campId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserCamp userCamp = (UserCamp) o;
		return campId == userCamp.campId &&
				Objects.equal(userId, userCamp.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userId, campId);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("UserCamp{");
		sb.append("userId='").append(userId).append('\'');
		sb.append(", campId=").append(campId);
		sb.append('}');
		return sb.toString();
	}
}
