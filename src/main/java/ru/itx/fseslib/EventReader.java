/*
 * Copyright 2010 Eugene Prokopiev <enp@itx.ru>
 *
 * This file is part of FSESLib (FreeSWITCH Event Socket Library).
 *
 * FSESLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FSESLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TXMLib. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package ru.itx.fseslib;

import java.io.BufferedReader;
import java.net.SocketException;

/**
 * @author Eugene Prokopiev <enp@itx.ru>
 *
 */
class EventReader implements Runnable {

	private EventListener eventListener;
	private BufferedReader eventSocketReader;

	public EventReader(EventListener eventListener, BufferedReader eventSocketReader) {
		this.eventListener = eventListener;
		this.eventSocketReader = eventSocketReader;
	}

	public void run() {
		String line;
		Event event = null;
		try {
			while((line = eventSocketReader.readLine()) != null) {
				if (!line.isEmpty()) {
					if (event == null)
						event = new Event();
					event.add(line);
				} else if (event != null) {
					if (event.getName() != null)
						eventListener.handleEvent(event);
					event = null;
				}
			}
		} catch (SocketException e) {
			if (!e.getMessage().equals("Socket closed"))
				throw new Error("Event socket read error : "+e);
		} catch (Exception e) {
			throw new Error("Event socket read error : "+e);
		}
	}
}
