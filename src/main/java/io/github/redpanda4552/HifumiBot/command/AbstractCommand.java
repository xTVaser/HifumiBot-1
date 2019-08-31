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
package io.github.redpanda4552.HifumiBot.command;

import io.github.redpanda4552.HifumiBot.HifumiBot;
import io.github.redpanda4552.HifumiBot.util.CommandMeta;

public abstract class AbstractCommand {

    protected static final String CATEGORY_BUILTIN = "builtin", CATEGORY_NONE = "none";
    
    protected HifumiBot hifumiBot;
    protected boolean admin;
    protected String category;
    
    public AbstractCommand(HifumiBot hifumiBot, boolean admin, String category) {
        this.hifumiBot = hifumiBot;
        this.admin = admin;
        this.category = category != null ? category : CATEGORY_NONE;
    }
    
    /**
     * Do a prelimiary permissions check, and execute if it passes.
     */
    public void run(CommandMeta cm) {
        if (!isAdminCommand() || hifumiBot.getPermissionManager().hasPermission(cm.getMember(), cm.getUser()))
            onExecute(cm);
    }
    
    /**
     * Command payload.
     */
    protected abstract void onExecute(CommandMeta cm);
    
    protected boolean isArgSingleWord(String arg) {
        return !arg.contains(" ");
    }
    
    public boolean isAdminCommand() {
        return admin;
    }
    
    public String getCategory() {
        return category;
    }
    
    protected abstract String getHelpText();
}
