package com.tugalsan.spi.helloworld;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.network.server.TS_NetworkSSLUtils;
import com.tugalsan.api.sql.conn.server.TS_SQLConnAnchor;
import com.tugalsan.api.sql.select.server.TS_SQLSelectUtils;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.tomcat.server.TS_TomcatPathUtils;
import java.nio.file.Path;
import javax.servlet.*;
import javax.servlet.annotation.*;

@WebListener
public class AppServlet implements ServletContextListener {

    final private static TS_Log d = TS_Log.of(AppServlet.class);

    public static String APP_NAME;

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        APP_NAME = TS_TomcatPathUtils.getWarNameLabel(evt);
        TS_LogUtils.MAP = txt -> TGS_StringUtils.cmn().concat("[", APP_NAME, "] ", txt);
        d.ci("contextInitialized", "coloring console...");
        TS_LogUtils.setColoredConsole(true);
        d.ci("contextInitialized", "disableing ssl validation...");
        TS_NetworkSSLUtils.disableCertificateValidation();
        d.ci("contextInitialized", "reading db config...");
        var u_dbConfig = TS_SQLConnAnchor.of(Path.of("C:\\dat\\sql\\cnn"), "autosqlweb");
        if (u_dbConfig.isExcuse()) {
            d.ce("contextInitialized", u_dbConfig.excuse().getMessage());
            return;
        }
        d.ci("contextInitialized", "reading table values...");
        TS_SQLSelectUtils.select(u_dbConfig.value(), "aktif").columnsAll()
                .whereConditionNone().groupNone().orderNone()
                .rowIdxOffsetNone().rowSizeLimitNone()
                .walkRows(TGS_FuncMTU_In1.empty, (rs, ri) -> {
                    d.cr("contextInitialized", "aktif", "walkRows", ri, rs.lng.get(0), rs.str.get(1));
                });
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        d.ci("contextDestroyed", "destroying TS_LogUtils...");
        TS_LogUtils.destroy();
        d.ci("contextDestroyed", "CONTEXT DESTROYED");
    }
}
