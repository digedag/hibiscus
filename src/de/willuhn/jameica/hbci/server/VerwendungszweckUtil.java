/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus/src/de/willuhn/jameica/hbci/server/VerwendungszweckUtil.java,v $
 * $Revision: 1.3 $
 * $Date: 2008/05/30 12:02:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.server;

import java.rmi.RemoteException;
import java.util.ArrayList;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Transfer;
import de.willuhn.jameica.hbci.rmi.Verwendungszweck;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Hilfsklasse zum Laden und Speichern der erweiterten Verwendungszwecke.
 */
public class VerwendungszweckUtil
{
  /**
   * Liefert eine Liste der gefundenen Verwendungszwecke fuer diesen Transfer.
   * @param t der Transfer.
   * @return Liste der gefundenen Verwendungszwecke, sortiert nach ID oder null, wenn der Transfer neu ist.
   * @throws RemoteException
   */
  public static GenericIterator get(Transfer t) throws RemoteException
  {
    if (!(t instanceof DBObject))
      return null;
    
    DBObject g = (DBObject) t;
    if (g.isNewObject())
      return null;
    
    DBIterator list = Settings.getDBService().createList(Verwendungszweck.class);
    list.addFilter("typ = " + t.getTransferTyp());
    list.addFilter("auftrag_id = " + g.getID());
    list.setOrder("order by id");
    return list;
  }
  
  /**
   * Wandelt die Liste der erweiterten Verwendungszwecke des Transfers in ein
   * Array um.
   * @param transfer der Transfer.
   * @return Erweiterte Verwendungszwecke als String-Array. Nie null sondern hoechstens ein leeres Array
   * @throws RemoteException
   */
  public static String[] toArray(Transfer transfer) throws RemoteException
  {
    GenericIterator it = transfer.getWeitereVerwendungszwecke();
    if (it == null)
      return new String[0]; // Keine Verwendungszwecke
    ArrayList list = new ArrayList();
    while (it.hasNext())
    {
      Verwendungszweck z = (Verwendungszweck) it.next();
      list.add(z.getText());
    }
    return (String[]) list.toArray(new String[list.size()]);
  }

  /**
   * Legt einen neuen Verwendungszweck fuer den Transfer an.
   * Der Verwendungszweck wird sofort gespeichert.
   * @param t der Transfer.
   * @param text der zu speichernde Text.
   * @return der erstellte Verwendungszweck.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static Verwendungszweck create(Transfer t, String text) throws RemoteException, ApplicationException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(HBCI.class).getResources().getI18N();

    if (text == null || text.length() == 0)
      throw new ApplicationException(i18n.tr("Kein Verwendungszweck angegeben"));
    
    if (!(t instanceof DBObject))
      throw new ApplicationException(i18n.tr("Verwendungszweck kann f�r diesen Datensatz-Typ nicht angelegt werden"));
    
    DBObject g = (DBObject) t;
    if (g.isNewObject())
      throw new ApplicationException(i18n.tr("Bitte speichern Sie zuerst den Auftrag"));
    
    Verwendungszweck zweck = (Verwendungszweck) Settings.getDBService().createObject(Verwendungszweck.class,null);
    zweck.apply(t);
    zweck.setText(text);
    zweck.store();
    return zweck;
  }
}


/*********************************************************************
 * $Log: VerwendungszweckUtil.java,v $
 * Revision 1.3  2008/05/30 12:02:08  willuhn
 * @N Erster Code fuer erweiterte Verwendungszwecke - NOCH NICHT FREIGESCHALTET!
 *
 * Revision 1.2  2008/02/22 00:52:35  willuhn
 * @N Erste Dialoge fuer erweiterte Verwendungszwecke (noch auskommentiert)
 *
 * Revision 1.1  2008/02/15 17:39:10  willuhn
 * @N BUGZILLA 188 Basis-API fuer weitere Zeilen Verwendungszweck. GUI fehlt noch
 * @N DB-Update 0005. Speichern des Textschluessels bei Sammelauftragsbuchungen in der Datenbank
 *
 **********************************************************************/