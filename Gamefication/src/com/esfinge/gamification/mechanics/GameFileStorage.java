package com.esfinge.gamification.mechanics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.esfinge.gamification.achievement.Achievement;
import com.esfinge.gamification.achievement.Point;
import com.esfinge.gamification.achievement.Ranking;
import com.esfinge.gamification.achievement.Reward;
import com.esfinge.gamification.achievement.Trophy;
import com.esfinge.gamification.factory.AchievementFactory;
import com.esfinge.gamification.listener.AchievementListener;

public class GameFileStorage extends Game {
	
	private String key;	
	private Properties props;
	private static File dir = null;
	private ArrayList<AchievementListener> ac = new ArrayList<>();
	private static String delim = "|";
	
	public GameFileStorage(String fileName) {
		this.dir = new File(fileName);		  
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esfinge.gamefication.mechanics.Game#addAchievement(java.lang.Object,
	 * com.esfinge.gamefication.achievement.Achievement)
	 */
	@Override
	public void insertAchievement(Object user, Achievement a) {
		String className = a.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);

		key = user.toString() + delim + className + delim + a.getName();

		try {
			props = new Properties();
			FileInputStream inputFile = new FileInputStream(dir);
			props.load(inputFile);
					
			if (a instanceof Point) {
				props.setProperty(key, ((Point) a).getQuantity().toString());
			}
			else if (a instanceof Ranking) {
				props.setProperty(key, ((Ranking) a).getLevel().toString());
			}
			else if (a instanceof Reward) {
				props.setProperty(key,
						((Boolean) ((Reward) a).isUsed()).toString());
			}
			else if (a instanceof Trophy) {
				props.setProperty(key, "");
			}

			FileOutputStream outputFile = new FileOutputStream(dir);
			props.store(outputFile, null);
			outputFile.close();

		} catch (IOException e) {
			throw new RuntimeException("Achievement '"+key+"' could not be written into file properly."+e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esfinge.gamefication.mechanics.Game#removeAchievement(java.lang.Object
	 * , com.esfinge.gamefication.achievement.Achievement)
	 */
	@Override
	public void deleteAchievement(Object user, Achievement a) {
		String className = a.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);

		key = user.toString() + delim + className + delim
				
				+ a.getName();

		try {
			props = new Properties();
			FileInputStream inputFile = new FileInputStream(dir);
			props.load(inputFile);
			
			props.remove(key);
			
			FileOutputStream outputFile = new FileOutputStream(dir);
			props.store(outputFile, null);
			outputFile.close();

		} catch (IOException e) {
			throw new RuntimeException("Achievement '"+key+"' could not be deleted from file properly."+e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esfinge.gamefication.mechanics.Game#getAchievement(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public Achievement getAchievement(Object user, String achievName) {
		Properties prop;
		Achievement a = null;
		try {
			
			prop = new Properties();
			FileInputStream inputFile = new FileInputStream(dir);
			prop.load(inputFile);

			for (String key : prop.stringPropertyNames()) {
				String userName = key.substring(0, key.indexOf(delim));
				String achievementType = key.substring(key.indexOf(delim) + 1,
						key.lastIndexOf(delim));
				String achievementName = key
						.substring(key.lastIndexOf(delim) + 1);
				String achievementValue = prop.getProperty(key);

				if (user.toString().equals(userName) && achievementName.equals(achievName)) {
					
					a = AchievementFactory.createAchievement(achievementType, achievementName, achievementValue);
				}
			}

			FileOutputStream file = new FileOutputStream(dir);
			prop.store(file, null);
			file.close();			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esfinge.gamefication.mechanics.Game#getAchievements(java.lang.Object)
	 */
	@Override
	public Map<String, Achievement> getAchievements(Object user) {
		Properties prop;
		Map<String, Achievement> achievements = new HashMap<String, Achievement>();
		Achievement a;
	
		try {
			prop = new Properties();
			FileInputStream inputFile = new FileInputStream(dir);
			prop.load(inputFile);

			for (String key : prop.stringPropertyNames()) {
				String userName = key.substring(0, key.indexOf(delim));
				String achievementType = key.substring(key.indexOf(delim) + 1,
						key.lastIndexOf(delim));
				String achievementName = key
						.substring(key.lastIndexOf(delim) + 1);
				String achievementValue = prop.getProperty(key);

				if (user.toString().equals(userName)) {
					
					a = AchievementFactory.createAchievement(achievementType, achievementName, achievementValue);
					achievements.put(a.getName(), a);
				}
			}

			FileOutputStream file = new FileOutputStream(dir);
			prop.store(file, null);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return achievements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esfinge.gamefication.mechanics.Game#addListener(com.esfinge.gamefication
	 * .listener.AchievementListener)
	 */
	@Override
	public void addListener(AchievementListener listener) {
		ac.add(listener);
	}

	private void notify(AchievementListener listener) {
		for (AchievementListener a : ac) {
			a.achievementAdded(a, null);
		}
	}

	@Override
	public void updateAchievement(Object user, Achievement a) {
		this.insertAchievement(user, a);
		
	}
}
