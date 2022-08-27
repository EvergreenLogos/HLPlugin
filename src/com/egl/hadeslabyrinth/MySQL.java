package com.egl.hadeslabyrinth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.cedarsoftware.util.io.JsonWriter;

public class MySQL {
    private String host = "localhost";
    private String port = "3306";
    private String database = "HadesLabyrinth";
    private String username = "minecraft";
    private String password = "2317899208376";
    private Connection con;

    private Boolean connected;

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    private HLPlugin plugin;

    public MySQL(HLPlugin plugin) {
	this.plugin = plugin;

	connected = false;
    }

    public void connect() {
	if (!connected) {
	    try {
		con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,
			password);
		plugin.getLogger().info("Connecting to SQL Database...");
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    plugin.getLogger().info("Success!");
	    connected = true;
	}
    }

    public void disconnect() {
	if (connected) {
	    try {
		con.close();
		plugin.getLogger().info("Closing MySQL Connection...");
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    plugin.getLogger().info("Success!");
	    connected = false;
	}
    }

    public boolean isConnected() {
	return (con == null ? false : true);
    }

    public Connection getConnection() {
	return con;
    }

    private ResultSet getByKey(String tableName, String keyType, String key) {

	try {
	    Statement st = con.createStatement();
	    ResultSet res;
	    res = st.executeQuery("SELECT * FROM " + tableName + " WHERE " + keyType + " = '" + key + "';");
	    return res;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    private Object getField(String tableName, String keyType, String key, String tag) throws SQLException {
	ResultSet res = getByKey(tableName, keyType, key);
	return res.getObject(tag);
    }

    private void setField(String tableName, String keyType, String key, String field, String value) {
	try {
	    Statement st = con.createStatement();
	    st.executeUpdate(
		    "UPDATE officials SET " + field + " = " + value + " WHERE " + keyType + " = '" + key + "';");
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public Boolean entryExists(String tableName, String key, String value) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM " + tableName + " WHERE " + key + " = '" + value + "';");
	return res.next();
    }

    public Object getPlayerData(String UUID, String tag) throws SQLException {
	// Statement st = con.createStatement();
	// ResultSet res = st.executeQuery("SELECT * FROM players WHERE UUID =
	// '"+UUID+"';");
	// res.next();
	// return res.getObject(tag);

	return getField("players", "UUID", UUID, tag);
    }

    public ArrayList<Object> getOfficialData(String UUID) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM officials WHERE UUID = '" + UUID + "';");
	res.next();
	ArrayList<Object> list = new ArrayList<Object>();

	java.sql.ResultSetMetaData rsmd = res.getMetaData();
	int count = rsmd.getColumnCount();

	for (int i = 1; i != count; i++) {
	    list.add(res.getObject(i));
	}

	return list;
    }

    public Object getStaffData(String username, String tag) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM staff WHERE Username = '" + username + "';");
	res.next();
	return res.getObject(tag);
    }

    public void setPlayerData(String UUID, String tag, Object value) throws SQLException {
	Statement st = con.createStatement();
	st.executeUpdate("UPDATE players SET " + tag + " = " + value.toString() + " WHERE UUID = '" + UUID + "';");
	plugin.getLogger().info("Updated player " + UUID + "'s field " + tag + ", setting it to " + value.toString());
    }

    public void setStaffData(String username, String tag, Object value) throws SQLException {
	Statement st = con.createStatement();
	st.executeUpdate(
		"UPDATE staff SET " + tag + " = " + value.toString() + " WHERE Username = '" + username + "';");
	plugin.getLogger()
		.info("Updated staff " + username + "'s field " + tag + ", setting it to " + value.toString());
    }

    public Object getRankData(int rank, String tag) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM rankPrices WHERE Num = '" + rank + "';");
	res.next();
	return res.getObject(tag);
    }

    public boolean playerHasEntry(String UUID) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM players WHERE UUID = '" + UUID + "';");
	return res.next();
    }

    void createPlayerEntry(Player player) throws SQLException {
	plugin.getLogger().info("Adding new MySQL entry for player " + player.getUniqueId().toString());
	Statement st = con.createStatement();
	st.executeUpdate("INSERT INTO players (UUID, Username, Rank, Prestige, VIP) VALUES ('"
		+ player.getUniqueId().toString() + "', '" + player.getName() + "', 0, 0, 0);");
    }

    void removePlayerEntry(String username) throws SQLException {
	plugin.getLogger().info("Removing MySQL entry for player " + username);
	Statement st = con.createStatement();
	st.executeUpdate("DELETE FROM players WHERE Username = '" + username + "';");
    }

    void createStaffEntry(Player player) throws SQLException {
	plugin.getLogger().info("Adding new MySQL entry for staff member " + player.getUniqueId().toString());
	Statement st = con.createStatement();
	st.executeUpdate("INSERT INTO staff (UUID, Username, Rank) VALUES ('" + player.getUniqueId().toString() + "', '"
		+ player.getName() + "', 9);");
    }

    void removeStaffEntry(String username) throws SQLException {
	plugin.getLogger().info("Removing MySQL entry for staff member " + username);
	Statement st = con.createStatement();
	st.executeUpdate("DELETE FROM staff WHERE Username = '" + username + "';");
    }

    public void reconnect() {
	plugin.getLogger().info("Refreshing database connection...");
	disconnect();
	connect();
	plugin.getLogger().info("Refresh okay!");
    }

    public String getRandomBroadcast() throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM broadcasts ORDER BY RAND() LIMIT 1;");
	res.next();
	return res.getObject("text").toString();
    }

    // public void serveDonors() throws SQLException
    // {
    // Statement st = con.createStatement();
    // ResultSet res = st.executeQuery("SELECT COUNT(*) FROM duePlayers;");
    // res.next();
    // long num = (Long) res.getObject(1);
    // plugin.getLogger().info(num+" donors are due for rewards.");
    //
    // Statement st2 = con.createStatement();
    // ResultSet res2 = st2.executeQuery("SELECT * FROM duePlayers");
    // while (res2.next())
    // {
    // if (Bukkit.getOfflinePlayer(UUID.fromString(((String)
    // res2.getObject("UUID")).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
    // "$1-$2-$3-$4-$5"))).isOnline())
    // {
    // plugin.exec(res2.getObject("command").toString());
    // Float donation = (Float) res2.getObject("transactionAmount");
    // String UUID = (String) res2.getObject("UUID");
    //
    // Statement st3 = con.createStatement();
    // st3.executeUpdate("UPDATE players SET donations = donations +
    // "+donation+" WHERE UUID = '"+UUID+"';");
    //
    // Statement st4 = con.createStatement();
    // st4.executeUpdate("DELETE FROM duePlayers WHERE UUID = '"+UUID+"';");
    // } else {
    // plugin.getLogger().info("Player with UUID "+res2.getObject("UUID")+" is
    // not currently online, postponing reward.");
    // }
    // }
    //
    // //for (int i=0; i!=num; i++)
    // //{
    // // Statement st2 = con.createStatement();
    // // ResultSet res2 = st2.executeQuery("SELECT * FROM duePlayers WHERE 'ID'
    // = "+i+";");
    // // res2.next();
    // // plugin.exec(res2.getObject("command").toString());
    // // Float donation = (Float) res2.getObject("transactionAmount");
    // // String UUID = (String) res2.getObject("UUID");
    //
    // // Statement st3 = con.createStatement();
    // // st3.executeUpdate("UPDATE players SET donations = donations +
    // "+donation+" WHERE UUID = '"+UUID+"';");
    // //}
    //
    // //Statement st4 = con.createStatement();
    // //st4.executeUpdate("DELETE FROM duePlayers;");
    // }

    // @SuppressWarnings("deprecation")
    // public void startMonthlyPass(OfflinePlayer player, int tier) throws
    // SQLException
    // {
    // Calendar cal = Calendar.getInstance();
    // cal.add(Calendar.DATE, 30);
    //
    // Date expDate = cal.getTime();
    // java.sql.Date ExpDateSQL = new java.sql.Date(expDate.getTime());
    //
    // plugin.getLogger().info("Adding new monthly pass entry for player
    // "+player.getUniqueId().toString());
    // java.sql.PreparedStatement pstmt = con.prepareStatement("INSERT INTO
    // monthlyPass (UUID, Username, Tier, ExpirationDate) VALUES (?, ?, ?,
    // ?);");
    // pstmt.setString(1, player.getUniqueId().toString());
    // pstmt.setString(2, player.getName());
    // pstmt.setInt(3, tier);
    // pstmt.setDate(4, ExpDateSQL);
    // pstmt.execute();
    // plugin.getLogger().info("Success! The monthly pass will expire on
    // "+ExpDateSQL.toString());
    //
    // }
    //
    // public void endMonthlyPass(String player) throws SQLException
    // {
    // plugin.getLogger().info("Removing monthly pass entry for player
    // "+player);
    // Statement st = con.createStatement();
    // st.executeUpdate("DELETE FROM monthlyPass WHERE Username =
    // '"+player+"';");
    // }
    //
    // public int checkMonthlyPass(Player player) throws SQLException
    // {
    // Statement st = con.createStatement();
    // ResultSet res = st.executeQuery("SELECT * FROM monthlyPass WHERE UUID =
    // '"+player.getUniqueId().toString()+"';");
    // if (res.next())
    // {
    // return (int) res.getObject("Tier");
    // }
    // return -1;
    // }

    public HashMap<Integer, Integer> loadRankPrices(HashMap<Integer, Integer> map) throws SQLException {
	map.clear();
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM rankPrices;");
	for (int i = 1; i != 26; i++) {
	    res.next();
	    int cost = Integer.parseInt(res.getObject("Price").toString());
	    map.put(i, cost);
	    plugin.getLogger().info("Rank " + HLPlugin.getChar(i - 1) + " costs " + cost + ".");
	}
	return map;
    }

    public void saveSlaveData(Slave slave) throws SQLException {
	Statement st = con.createStatement();
	st.executeUpdate("INSERT INTO slaves (UUID, name, type, master) VALUES ('" + slave.getID().toString() + "', '"
		+ slave.getName() + "', " + slave.getType() + ", '" + slave.getMaster().toString() + "');");
    }

    public void deleteSlaveData(UUID id) {
	try {
	    Statement st = con.createStatement();
	    st.executeUpdate("DELETE FROM slaves WHERE UUID = '" + id.toString() + "';");
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void updateSlaveData(Slave slave) throws SQLException {
	Statement st = con.createStatement();

	// plugin.getLogger().info(slave.getName());
	// plugin.getLogger().info(String.valueOf(slave.getType()));
	// plugin.getLogger().info(String.valueOf(slave.getMaster()));
	// plugin.getLogger().info(String.valueOf(slave.getLastX()));
	// plugin.getLogger().info(String.valueOf(slave.getLastY()));
	// plugin.getLogger().info(String.valueOf(slave.getLastZ()));
	// plugin.getLogger().info(slave.getLastWorld());
	// plugin.getLogger().info(slave.getID().toString());

	st.executeUpdate("UPDATE slaves SET name = '" + slave.getName() + "', type = " + slave.getType()
		+ ", master = '" + slave.getMaster() + "', lastX = " + slave.getLastX() + ", lastY = "
		+ slave.getLastY() + ", lastZ = " + slave.getLastZ() + ", lastWorld = '" + slave.getLastWorld()
		+ "', level = " + slave.getLevel() + ", skin = " + slave.getSkin()
		// +", data =
		// '"+JsonWriter.objectToJson(slave.getData())
		+ ", data = '" + toBase64(slave.getData()) + "' WHERE UUID = '" + slave.getID().toString() + "';");
    }

    public Slave loadSlaveData(UUID uuid) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM slaves WHERE UUID = '" + uuid.toString() + "';");

	res.next();
	String name = res.getString("name");
	int type = res.getInt("type");
	UUID master = UUID.fromString(res.getString("master"));

	plugin.getLogger().info("Slaves type is: " + type);

	int lastX = res.getInt("lastX");
	int lastY = res.getInt("lastY");
	int lastZ = res.getInt("lastZ");
	String lastWorld = res.getString("lastWorld");

	int lvl = res.getInt("level");
	int skin = res.getInt("skin");

	String data = res.getString("data");

	Slave slave = new Slave(this.plugin, uuid);
	slave.setName(name);
	slave.setType(type);
	slave.setMaster(master);

	plugin.getLogger().info("" + slave.getType());

	slave.setLastPosition(lastX, lastY, lastZ, lastWorld);

	slave.setLevel(lvl);
	slave.setSkin(skin);

	plugin.getLogger().info(data);

	if (data != null) {
	    plugin.getLogger().info("There is data");
	    // Gson gson = new Gson();
	    // slave.setData(gson.fromJson(res.getString("data"),
	    // ArrayList.class));
	    slave.setData(fromBase64(data));
	}

	return slave;
    }

    public UUID lookupSlave(UUID ownerID, String slaveName) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery(
		"SELECT * FROM slaves WHERE master = '" + ownerID.toString() + "' AND name = '" + slaveName + "';");

	res.next();

	return UUID.fromString(res.getString("UUID"));
    }

    public ArrayList<UUID> listPlayersSlaves(UUID playerID) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM slaves WHERE master = '" + playerID.toString() + "';");

	ArrayList<UUID> list = new ArrayList<UUID>();

	while (res.next()) {
	    list.add(UUID.fromString(res.getString("UUID")));
	}

	return list;
    }

    public ArrayList<String> listPlayersSlavesNames(UUID playerID) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM slaves WHERE master = '" + playerID.toString() + "';");

	ArrayList<String> list = new ArrayList<String>();

	while (res.next()) {
	    list.add(res.getString("name"));
	}

	return list;
    }

    public ArrayList<String> getSkin(int id) throws SQLException {
	Statement st = con.createStatement();
	ResultSet res = st.executeQuery("SELECT * FROM skins WHERE id = " + id + ";");
	res.next();
	ArrayList<String> returnMe = new ArrayList<String>();
	returnMe.add(res.getString("texture"));
	returnMe.add(res.getString("signature"));

	return returnMe;
    }

    public String toBase64(ArrayList<Object> list) {
	try {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

	    dataOutput.writeInt(list.size());

	    for (int i = 0; i < list.size(); i++) {
		dataOutput.writeObject(list.get(i));
	    }

	    dataOutput.close();

	    return Base64Coder.encodeLines(outputStream.toByteArray());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    public ArrayList<Object> fromBase64(String st) {
	try {
	    ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(st));
	    BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

	    ArrayList<Object> list = new ArrayList<Object>();

	    int size = dataInput.readInt();
	    for (int i = 0; i < size; i++) {
		list.add((Object) dataInput.readObject());
	    }

	    dataInput.close();

	    return list;
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    public String getRandomName(char sex) {
	String result = "";
	try {
	    Statement st = con.createStatement();
	    ResultSet res;
	    res = st.executeQuery("SELECT * FROM fantasyNames WHERE sex = '" + sex + "' ORDER BY RAND() LIMIT 1;");
	    res.next();
	    result = res.getString("name");
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return result;
    }

    public ArrayList<Object> getNodeData(String UUID) throws SQLException {
	ResultSet res = getByKey("nodes", "UUID", UUID);
	ArrayList<Object> listOfNodes = new ArrayList<Object>();

	while (res.next()) {
	    ArrayList<Object> nodeData = new ArrayList<Object>();
	    java.sql.ResultSetMetaData rsmd = res.getMetaData();
	    int count = rsmd.getColumnCount();

	    for (int i = 1; i != count; i++) {
		nodeData.add(res.getObject(i));
	    }

	    listOfNodes.add(nodeData);
	}

	return listOfNodes;
    }

    public void setNodeData(String UUID, String tag, Object value) throws SQLException {
	Statement st = con.createStatement();
	st.executeUpdate("UPDATE officials SET " + tag + " = " + value.toString() + " WHERE UUID = '" + UUID + "';");
    }

    void createNodeEntry(Node node) throws SQLException {
	Statement st = con.createStatement();
	st.executeUpdate("INSERT INTO officials (UUID) VALUES (');");
    }

    void removeNodeEntry(String name) throws SQLException {
	Statement st = con.createStatement();
	st.executeUpdate("DELETE FROM officials WHERE UUID = '';");
    }
}
