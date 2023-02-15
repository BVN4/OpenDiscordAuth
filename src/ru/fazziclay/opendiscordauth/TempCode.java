package ru.fazziclay.opendiscordauth;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TempCode {

	// Static
	public static List<TempCode> codes = new ArrayList<>();

	public static void delete(TempCode tempCode) {
		Utils.debug("[TempCode] delete()");

		tempCode.cancelExpiredTimer();
		TempCode.codes.remove(tempCode);
	}

	public static String create(String ownerUUID, String ownerNickname) {
		Utils.debug("[TempCode] create()");
		String content;
		Timer expiredTimer = new Timer();

		int i = 0;
		while (true) {
			content = String.valueOf(Utils.getRandom(Config.codeCreatorMinimum, Config.codeCreatorMaximum));

			if (getByValue(0, content) == null) { // Ищем код по контенту. Если не нахоим то break
				Utils.debug("[TempCode] create(): while break i="+i);
				break;
			}

			if (i > 40) {
				Utils.debug("[TempCode] create(): returned 'null': reason:(i>40)");
				return null;
			}
			i++;
		}

		TempCode tempCode = new TempCode(content, ownerUUID, ownerNickname, expiredTimer);
		TempCode.codes.add(tempCode);

		return content;
	}

	public static TempCode getByValue(int type, Object value) {
		Utils.debug("[TempCode] getByValue("+type+", "+value+")");

		int i = 0;
		while (i < codes.size()) {
			TempCode currentTempCode = codes.get(i);

			if (type == 0 && currentTempCode.content.equals(value)) {
				Utils.debug("[TempCode] getByValue("+type+", "+value+"): returned '"+currentTempCode+"'");
				return currentTempCode;
			}

			if (type == 1 && currentTempCode.ownerUUID.equals(value)) {
				Utils.debug("[TempCode] getByValue("+type+", "+value+"): returned '"+currentTempCode+"'");
				return currentTempCode;
			}

			if (type == 2 && currentTempCode.ownerNickname.equals(value)) {
				Utils.debug("[TempCode] getByValue("+type+", "+value+"): returned '"+currentTempCode+"'");
				return currentTempCode;
			}

			if (type == 3 && currentTempCode.expiredTimer == value) {
				Utils.debug("[TempCode] getByValue("+type+", "+value+"): returned '"+currentTempCode+"'");
				return currentTempCode;
			}


			i++;
		}

		Utils.debug("[TempCode] getByValue("+type+", "+value+"): returned 'null'");
		return null;
	}


	//Not-static
	public String content;
	public String ownerUUID;
	public String ownerNickname;
	public Timer expiredTimer;

	public void delete() {
		Utils.debug("[TempCode] [object] delete()");
		TempCode.delete(this);
	}

	private void startExpiredTimer() {
		TempCode _this = this;

		this.expiredTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				_this.delete();

				if (!LoginManager.isAuthorized(ownerUUID)) {
					Utils.kickPlayer(Utils.getPlayerByUUID(_this.ownerUUID), Config.messageAuthorizationTimeout);
				}
			}
		}, Config.codeCreatorExpiredDelay * 1000L);
	}

	public void cancelExpiredTimer() {
		this.expiredTimer.cancel();
	}


	// Constructor
	public TempCode(String content, String ownerUUID, String ownerNickname, Timer expiredTimer) {
		Utils.debug("[TempCode] -> created new object: content="+content+"; ownerUUID="+ownerUUID+"");
		this.content = content;
		this.ownerUUID = ownerUUID;
		this.ownerNickname = ownerNickname;
		this.expiredTimer = expiredTimer;

		startExpiredTimer();
	}
}
