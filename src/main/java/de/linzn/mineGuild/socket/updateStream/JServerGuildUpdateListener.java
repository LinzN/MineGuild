/*
 * Copyright (C) 2018. MineGaming - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 *  You should have received a copy of the LGPLv3 license with
 *  this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.mineGuild.socket.updateStream;


import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.mineGuild.manager.InternalGuildManager;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildUpdateListener implements IncomingDataListener {


    @Override
    public void onEvent(String channel, UUID clientUUID, byte[] dataInBytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(dataInBytes));
        String subChannel;
        try {
            subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("add_experience_sync")) {
                JSONArray jsonArray = new JSONArray(in.readUTF());
                for (Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) object;
                    UUID guildUUID = UUID.fromString(jsonObject.getString("guildUUID"));
                    double data = jsonObject.getDouble("guildExperience");
                    InternalGuildManager.add_exp_to_guild(guildUUID, data);
                }
            }


        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
