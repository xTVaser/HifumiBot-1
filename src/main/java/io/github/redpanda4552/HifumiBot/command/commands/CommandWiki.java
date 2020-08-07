/**
 * This file is part of HifumiBot, licensed under the MIT License (MIT)
 * 
 * Copyright (c) 2018 Brian Wood
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.redpanda4552.HifumiBot.command.commands;

import java.util.HashMap;

import io.github.redpanda4552.HifumiBot.HifumiBot;
import io.github.redpanda4552.HifumiBot.command.CommandInterpreter;
import io.github.redpanda4552.HifumiBot.command.CommandMeta;
import io.github.redpanda4552.HifumiBot.wiki.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class CommandWiki extends AbstractCommand {

    public CommandWiki() {
        super("wiki", CATEGORY_BUILTIN, false);
    }

    @Override
    protected void onExecute(CommandMeta cm) {
        if (cm.getArgs().length == 0) {
            HifumiBot.getSelf().sendMessage(cm.getChannel(), "I can't search for nothing! Try `" + CommandInterpreter.PREFIX + "wiki <title of game here>`");
            return;
        }
        
        HashMap<String, Float> results = new HashMap<String, Float>();
        
        // A basic weighting algorithm.
        for (String name : HifumiBot.getSelf().getWikiIndex().getAllTitles()) {
            String[] nameParts = name.toLowerCase().trim().split(" ");
            float toPush = 0;
            
            // For each search term...
            for (String arg : cm.getArgs()) {
                boolean wasFullMatch = false;
                // For each (space delimited) part of the name...
                for (String namePart : nameParts) {
                    // If exact match...
                    if (namePart.equals(arg)) {
                        // One full point
                        toPush += 1.0;
                        wasFullMatch = true;
                        break;
                    }
                }
                
                // If not a full match...
                if (!wasFullMatch) {
                    // But the arg's char sequence exists arbitrarily in the title...
                    if (name.toLowerCase().trim().contains(arg.toLowerCase().trim())) {
                        // Half a point
                        toPush += 0.5;
                    } else {
                        // If not even, then detract half
                        toPush -= 0.5;
                    }
                }
            }
            
            toPush -= 0.1 * Math.abs(nameParts.length - cm.getArgs().length);
            
            if (toPush > 0)
                results.put(name, toPush);
        }
        
        EmbedBuilder eb = new EmbedBuilder();
        int i = 0;
        
        if (results.size() > 0) {
            eb.setTitle("Query Results");
            String highestName = null;
            float highestWeight = 0;
            
            while (!results.isEmpty() && i < 6) {
                for (String name : results.keySet()) {
                    if (results.get(name) > highestWeight) {
                        highestName = name;
                        highestWeight = results.get(name);
                    }
                }
                
                results.remove(highestName);
                
                eb.addField(String.valueOf(++i), highestName, false);
                highestWeight = 0;
            }
            
            eb.setFooter("Click the reaction number matching the game you are looking for.\nThis message will self-modify with it's wiki information.", HifumiBot.getSelf().getJDA().getSelfUser().getAvatarUrl());
        } else {
            eb.setTitle("No results matched your query!");
            eb.setColor(0xff0000);
        }
        
        Message msg = HifumiBot.getSelf().sendMessage(cm.getChannel(), eb.build());
        
        if (eb.getFields().size() == 1) {
            HifumiBot.getSelf().getEventListener().finalizeMessage(msg, eb.getFields().get(0).getValue(), cm.getUser().getId());
        } else {
            // String concatenation with unicodes is apparently punishable by build error, so we instead have this.
            if (i > 0)
                msg.addReaction(Emotes.ONE).complete();
            if (i > 1)
                msg.addReaction(Emotes.TWO).complete();
            if (i > 2)
                msg.addReaction(Emotes.THREE).complete();
            if (i > 3)
                msg.addReaction(Emotes.FOUR).complete();
            if (i > 4)
                msg.addReaction(Emotes.FIVE).complete();
            if (i > 5)
                msg.addReaction(Emotes.SIX).complete();
            
            HifumiBot.getSelf().getEventListener().waitForMessage(cm.getUser().getId(), msg);
        }
    }
    
    @Override
    public String getHelpText() {
        return "Search the PCSX2 wiki by game title";
    }
}
