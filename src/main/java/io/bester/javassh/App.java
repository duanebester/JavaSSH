package io.bester.javassh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App 
{
    // Setup Log
    public static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) 
    {
        StringBuilder outputBuffer = new StringBuilder();
        
        try 
        {
            // New JSCH
            JSch jsch = new JSch();
            
            // Property to not check for host keys
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            
            // Setup host and username
            Session session = jsch.getSession("monty", "monty.io", 22);
            
            // Setup password
            session.setPassword("password");
            
            // Add properties config
            session.setConfig(config);
            
            // Connect with timeout of 30 seconds
            session.connect(30000);
            
            // Create a channel to execute a command
            Channel channel = session.openChannel("exec");
            
            // Run a command -- list inside of home directory
            ((ChannelExec) channel).setCommand("cd ~; ls;");
            
            // channel exec connects, this runs command
            channel.connect();
            
            // Read the stream of data back from the command
            InputStream commandOutput = channel.getInputStream();
            int readByte = commandOutput.read();
            
            // While byte makes sense
            while (readByte != 0xffffffff) 
            {
                outputBuffer.append((char) readByte);
                readByte = commandOutput.read();
            }
            
            // Disconnect from exec channel
            channel.disconnect();
            
            // Disconnect from jsch session
            session.disconnect();
        } 
        catch (JSchException jse) 
        {
            LOG.error(jse.getMessage());
        } 
        catch (IOException ex) 
        {
            LOG.error(ex.getMessage());
        }
        
        // Display ssh command results
        LOG.info("SSH Output: \n{}", outputBuffer.toString());
        
        // Exit
        System.exit(0);
    }
}
