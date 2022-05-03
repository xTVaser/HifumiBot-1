/**
 * This file is part of HifumiBot, licensed under the MIT License (MIT)
 * 
 * Copyright (c) 2020 RedPanda4552 (https://github.com/RedPanda4552)
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
package io.github.redpanda4552.HifumiBot.command.slash;

import io.github.redpanda4552.HifumiBot.HifumiBot;
import io.github.redpanda4552.HifumiBot.command.AbstractSlashCommand;
import io.github.redpanda4552.HifumiBot.permissions.PermissionLevel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandPanic extends AbstractSlashCommand {

    public CommandPanic() {
        super(PermissionLevel.SUPER_ADMIN);
    }

    @Override
    protected void onExecute(SlashCommandEvent event) {
        OptionMapping enableOpt = event.getOption("enable");
        
        if (enableOpt == null) {
            event.reply("Missing required argument 'enable'").setEphemeral(true).queue();
            return;
        }
        
        event.deferReply().queue();
        
        if (enableOpt.getAsBoolean()) {
            enable(event);
        } else {
            disable(event);
        }
    }

    @Override
    protected CommandData defineSlashCommand() {
        return new CommandData("panic", "Panic mode to restrict messaging and server joins")
                .addOption(OptionType.BOOLEAN, "enable", "Enable or disable panic mode", true);
    }

    private void enable(SlashCommandEvent event) {
        if (HifumiBot.getSelf().getEventListener().getLockdown()) {
            event.getHook().editOriginal("Panic mode is already enabled").queue();
            return;
        }
        
        event.getGuild().getTextChannels().forEach((channel) -> {
            channel.getManager().setSlowmode(1).queue();
        });
        
        HifumiBot.getSelf().getEventListener().setLockdown(true);
        event.getHook().editOriginal("Panic mode activated.\n- 1 second slow mode is applied to all channels (including restricted channels)\n- New users are being instantly kicked but will receive a PM explaining why\n- Any users without roles are having messages automatically deleted.").queue();
    }
    
    private void disable(SlashCommandEvent event) {
        event.getGuild().getTextChannels().forEach((channel) -> {
            channel.getManager().setSlowmode(0).queue();
        });
        
        HifumiBot.getSelf().getEventListener().setLockdown(false);
        event.getHook().editOriginal("Panic mode deactivated. All previous changes have been reverted.").queue();
    }
}