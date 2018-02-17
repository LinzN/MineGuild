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

package de.linzn.mineGuild.socket;

import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JServerGuildOutput {

    public static void deletePlayerFromGuild(String pname, String uuid)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("DeleteGuildFromPlayer");
            dataOutputStream.writeUTF(pname);
            dataOutputStream.writeUTF(uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineGuild", byteArrayOutputStream.toByteArray());
    }

}
