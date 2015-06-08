package org.kurento.room.test;

/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * Room demo integration test (basic version).
 *
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @since 5.0.0
 */
public class NUsersEqualLifetimeRoomBasicTest extends RoomTestBase {

	private Logger log = LoggerFactory
			.getLogger(NUsersEqualLifetimeRoomBasicTest.class);

	private static final int PLAY_TIME = 5; // seconds

	private static final int NUM_USERS = 4;
	private static final String ROOM_NAME = "room";

	@Test
	public void test() throws Exception {

		parallelUsers(NUM_USERS, new UserLifecycle() {
			@Override
			public void run(int numUser, final WebDriver browser)
					throws InterruptedException, ExecutionException {

				final String userName = "user" + numUser;

				joinToRoom(browser, userName, ROOM_NAME);
				log.info("User '{}' joined to room '{}'", userName, ROOM_NAME);

				final long start = System.currentTimeMillis();

				parallelTask(NUM_USERS, new Function<Integer, Void>() {
					@Override
					public Void apply(Integer num) {
						String videoUserName = "user" + num;
						waitForStream(browser, "native-video-user" + num
								+ "_webcam");
						long duration = System.currentTimeMillis() - start;
						log.info(
								"Video received in browser of user {} for user '{}' in {} millis",
								userName, videoUserName, duration);
						return null;
					}
				});

				Thread.sleep(PLAY_TIME * 1000);

				log.info("User '{}' exiting from room '{}'", userName,
						ROOM_NAME);
				exitFromRoom(browser);
				log.info("User '{}' exited from room '{}'", userName, ROOM_NAME);

			}
		});
	}

}