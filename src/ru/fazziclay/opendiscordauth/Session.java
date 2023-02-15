package ru.fazziclay.opendiscordauth;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Session {

	// Static
	public static List<Session> sessions = new ArrayList<>();


	public static void create(String nickname, String ip) {
		Session session = new Session(nickname, ip);
		Session.sessions.add(session);
	}

	public static void delete(Session session) {
		Utils.debug("[Session] delete()");

		session.cancelExpiredTimer();
		Session.sessions.remove(session);
	}

	public static void update(String nickname, String ip) {
		Utils.debug("[Session] update()");

		Session session = getByValue(0, nickname);
		if (session != null) {
			session.reloadExpiredTimer();
			if (!session.ip.equals(ip)) {
				session.ip = ip;
			}

		} else {
			create(nickname, ip);
		}
	}

	public static boolean isActive(String nickname, String ip) {
		Utils.debug("[Session] isActive()");

		Session session = getByValue(0, nickname);
		return (session != null && session.ip.equals(ip));
	}

	public static Session getByValue(int type, Object value) {
		int i = 0;
		while (i < sessions.size()) {
			Session currentSession = sessions.get(i);

			if (type == 0 && currentSession.nickname.equals(value)) {
				Utils.debug("[Session] getByValue(" + type + ", " + value + "): returned '" + currentSession + "'");
				return currentSession;
			}

			i++;
		}

		Utils.debug("[Session] getByValue(" + type + ", " + value + "): returned 'null'");
		return null;
	}

	// Not static
	public String nickname;
	public String ip;
	public Timer expiredTimer;

	private void startExpiredTimer() {
		Session _this = this;

		this.expiredTimer = new Timer();
		this.expiredTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				_this.delete();
			}
		}, Config.sessionExpiredDelay * 1000L);
	}

	public void delete() {
		Utils.debug("[Session] [object] delete()");
		Session.delete(this);
	}

	public void reloadExpiredTimer() {
		this.expiredTimer.cancel();
		this.startExpiredTimer();
	}

	public void cancelExpiredTimer() {
		this.expiredTimer.cancel();
	}

	public Session(String nickname, String ip) {
		Utils.debug("[Session] created new object. nickname="+nickname+"; ip="+ip);
		this.nickname = nickname;
		this.ip = ip;
		startExpiredTimer();
	}
}
