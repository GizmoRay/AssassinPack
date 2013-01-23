package com.gizmoray.assassinpack;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PluginListener implements Listener {
	private AssassinPack plugin;
	boolean x = false;
	int HeadshotDamage;
	String WN;
	public Map<String, Long> cooldown= new HashMap <String, Long>();
	public int COOLDOWN_TIME = 5;
	
	public PluginListener(AssassinPack plugin){
		this.plugin=plugin;
		this.HeadshotDamage = plugin.getConfig().getInt("HeadshotDamage");
		this.WN = plugin.getConfig().getString("WorldNames");
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerIneract(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
		if(event.getPlayer().getItemInHand().getType() == Material.FEATHER){
		if(p.hasPermission("assassin.longjump") || p.isOp()){
			if(p.getItemInHand().getAmount() == 1){
				p.setItemInHand(new ItemStack(Material.AIR, 1));
			}
			else if (p.getItemInHand().getAmount() > 1){
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			
			
//				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 2));
				launch(event.getPlayer());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event){
		if((event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE) || x==true){
			if(!(event.getEntity() instanceof Player)) return;
				if(event.getCause().equals(DamageCause.FALL)){
					event.setCancelled(true);
					x=false;
				}
			}
		EntityType e = event.getEntity().getType();
		if((e == EntityType.PLAYER)){
		if (((Player) event.getEntity()).hasPermission("assassin.roll") || ((Player) event.getEntity()).isOp()) {
			if(((Player)event.getEntity()).getFoodLevel() > 3){
		if(!(event.getEntity() instanceof Player)) return;
		if(((Player) event.getEntity()).isSneaking()){
			if(event.getCause().equals(DamageCause.FALL)){
				if(event.getEntity().getFallDistance() < 15){
				event.setCancelled(true);
				event.getEntity().setFallDistance(4);
				((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, 40));
				int test = ((Player) event.getEntity()).getFoodLevel();
				test = test-4;
				((Player) event.getEntity()).setFoodLevel(test);
				
						}else{
							((Player) event.getEntity()).sendMessage(ChatColor.RED + "~You have fallen too far~");
							}
						}
					}
				}
			}
		}
	}
	

	@SuppressWarnings("static-access")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	 public void onShot(EntityDamageByEntityEvent event)
    {
        //only interested in projetile damage
        if(event.getCause() != DamageCause.PROJECTILE)
            return;
 
        Projectile proj = (Projectile)event.getDamager();
        //could be skeleton or dispenser, we only want player
        if(!(proj.getShooter() instanceof Player))
            return;
        if(proj.getType() != EntityType.ARROW) return;
        Entity victim = event.getEntity();
        EntityType victimType = victim.getType();
 
        double projY = proj.getLocation().getY() + proj.getVelocity().getY() / 2d;
        double victimY = victim.getLocation().getY();
        boolean headshot = projY - victimY > getBodyHeight(victimType);
        
        if(((Player) proj.getShooter()).hasPermission("assassin.headshot") && !event.getEntity().getServer().getPluginManager().isPluginEnabled("CombatCrits")){
        if(headshot && victimType == (event.getEntity().getType().PLAYER))
        {
        	String color = "BLUE";
            ((Player) event.getEntity()).sendMessage(ChatColor.valueOf(color) + "[Headshot BY " + (((Player) proj.getShooter()).getName() + "]"));
            ((Player) proj.getShooter()).sendMessage(ChatColor.DARK_RED + "[Headshot ON " +((Player) event.getEntity()).getName() + "]");
            ((Player) proj.getShooter()).playSound(proj.getShooter().getLocation(), Sound.ORB_PICKUP, 1000, 1000);
            ((LivingEntity) victim).damage(event.getDamage() + 2);
            event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.STEP_SOUND, 55);
            proj.remove();
            return;
     	   }
        }
    }
	
	@SuppressWarnings("static-access")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onArrow(ProjectileHitEvent event){
		double loc1;
		double loc2;
		double loc3;
		double loc4;
		Projectile proj = (Projectile)event.getEntity();
		loc2 = proj.getLocation().getX();
		loc4 = proj.getLocation().getZ();
		
		loc1 = event.getEntity().getShooter().getLocation().getX();
		loc3 = event.getEntity().getShooter().getLocation().getZ();
		double locn1 = loc3-loc4;
		double locn = loc1-loc2;
		if(proj.getType() != EntityType.ARROW) return;
		if(event.getEntity().getShooter().getType() == event.getEntity().getType().PLAYER){
		if(((Player) proj.getShooter()).isOp() == true || ((Player) proj.getShooter()).hasPermission("assassin.arrowspecials")){	
		if(((Player) event.getEntity().getShooter()).isSneaking()){
			if(((Player) event.getEntity().getShooter()).getItemInHand().getType() == Material.SULPHUR && ((Player) event.getEntity().getShooter()).getGameMode().getValue() == 0){
				if(locn1 < 0){
					locn1 = loc4-loc3;
				}
				
				if(locn < 0){
					locn = loc2-loc1;
				}
				if (((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() < 20){
					((Player) event.getEntity().getShooter()).sendMessage(ChatColor.GRAY + "You do not have the correct amount of resources to do that!");
					return;
				}
				
			if((locn > 50 && locn < 60) || (locn1 > 50 && locn1 < 60)){
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0f);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 20);
				}
					
			if((locn > 40 && locn < 50) || (locn1 > 40 && locn1 < 50)){
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0f);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 15);
				}
			
			if((locn > 30 && locn < 40) || (locn1 > 30 && locn1 < 40)){
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0f);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 10);
				}
			
			if((locn > 20 && locn < 30) || (locn1 > 20 && locn1 < 30)){
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0f);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 5);
				}
					
			if (locn < 10 || locn1 < 10){
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0f);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 5);
					}
			}
			
			if(((Player) event.getEntity().getShooter()).getItemInHand().getType() == Material.STRING && ((Player) event.getEntity().getShooter()).getGameMode().getValue() == 0){
				 if (cooldown.containsKey(((Player) event.getEntity().getShooter()).getName()))
				   {
				   long diff = (System.currentTimeMillis() - cooldown.get(((HumanEntity) event.getEntity().getShooter()).getName())) / 1000;
				   if (diff < COOLDOWN_TIME)
				   {
				   long remaining = COOLDOWN_TIME - diff;
				   ((Player) event.getEntity().getShooter()).sendMessage(ChatColor.BLUE + "You recently underwent a teleport! Please wait " + ChatColor.AQUA + remaining + ChatColor.BLUE + " seconds, or be incinerated!");
				   return;
				   }
				   }
				if(locn1 < 0){
					locn1 = loc4-loc3;
				}
				
				if(locn < 0){
					locn = loc2-loc1;
				}
				if (((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() < 25){
					((Player) event.getEntity().getShooter()).sendMessage(ChatColor.GRAY + "You do not have the correct amount of resources to grapple!");
					return;
				}
				
			if((locn > 50 && locn < 60) || (locn1 > 50 && locn1 < 60)){
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getShooter().damage(0);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				
				int  test = ((Player) event.getEntity().getShooter()).getFoodLevel();
				test = test-1;
				((Player) event.getEntity().getShooter()).setFoodLevel(test);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 25);
				cooldown.put(((Player) event.getEntity().getShooter()).getName(), System.currentTimeMillis());
				}
					
			if((locn > 40 && locn < 50) || (locn1 > 40 && locn1 < 50)){
				event.getEntity().getShooter().damage(0);
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				
				int  test = ((Player) event.getEntity().getShooter()).getFoodLevel();
				test = test-1;
				((Player) event.getEntity().getShooter()).setFoodLevel(test);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 20);
				cooldown.put(((Player) event.getEntity().getShooter()).getName(), System.currentTimeMillis());
				}
			
			if((locn > 30 && locn < 40) || (locn1 > 30 && locn1 < 40)){
				event.getEntity().getShooter().damage(0);
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				
				int  test = ((Player) event.getEntity().getShooter()).getFoodLevel();
				test = test-1;
				((Player) event.getEntity().getShooter()).setFoodLevel(test);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 15);
				cooldown.put(((Player) event.getEntity().getShooter()).getName(), System.currentTimeMillis());
				}
			
			if((locn > 20 && locn < 30) || (locn1 > 20 && locn1 < 30)){
				event.getEntity().getShooter().damage(0);
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				
				int  test = ((Player) event.getEntity().getShooter()).getFoodLevel();
				test = test-1;
				((Player) event.getEntity().getShooter()).setFoodLevel(test);
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 10);
				cooldown.put(((Player) event.getEntity().getShooter()).getName(), System.currentTimeMillis());
				}
					
			if (locn < 10 || locn1 < 10){
				event.getEntity().getShooter().damage(0);
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
				int  test = ((Player) event.getEntity().getShooter()).getFoodLevel();
				test = test-1;
				((Player) event.getEntity().getShooter()).setFoodLevel(test);
						((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 5);
						cooldown.put(((Player) event.getEntity().getShooter()).getName(), System.currentTimeMillis());
					}
			}
			if(!plugin.isRegioned(event.getEntity().getLocation()) == true){
				if(((Player) proj.getShooter()).hasPermission("assassin.torcharrow")){	
			if(((Player) event.getEntity().getShooter()).getItemInHand().getType() == Material.TORCH && ((Player) event.getEntity().getShooter()).getGameMode().getValue() == 0){
				if (((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() < 1){
					((Player) event.getEntity().getShooter()).sendMessage(ChatColor.GRAY + "You do not have the correct amount of resources to do that!");
					return;
				}
				Byte blockData = Byte.valueOf((byte)0);
				event.getEntity().getWorld().spawnFallingBlock(event.getEntity().getLocation(), Material.TORCH, blockData.byteValue());
				((HumanEntity) event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity) event.getEntity().getShooter()).getItemInHand().getAmount() - 1);
					}
				}
			}
			
			if(((Player) event.getEntity().getShooter()).getGameMode().getValue() == 1 && ((Player) event.getEntity().getShooter()).isOp() == true){
				if(locn1 < 0){
					locn1 = loc4-loc3;
				}
				
				if(locn < 0){
					locn = loc2-loc1;
				}
			if((locn > 50 && locn < 60) || (locn1 > 50 && locn1 < 60)){
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
					}
			if((locn > 40 && locn < 50) || (locn1 > 40 && locn1 < 50)){
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
						}
			if((locn > 30 && locn < 40) || (locn1 > 30 && locn1 < 40)){
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
						}
			if((locn > 20 && locn < 30) || (locn1 > 20 && locn1 < 30)){
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
			}
			if (locn < 10 || locn1 < 10){
				event.getEntity().getShooter().teleport(proj);
				event.getEntity().getWorld().playEffect(event.getEntity().getShooter().getLocation(), Effect.SMOKE, 900);
								}
							}
						}
					}
				}
			}

	
    private double getBodyHeight(EntityType type)
    {
        switch(type)
        {
            case PLAYER:
                return 1.4d;
         
            default:
                return Float.POSITIVE_INFINITY;
        }
    }
    
    public void launch(Player p){
		 p.setVelocity(new Vector(p.getVelocity().getX(), 0.7D, 0.0D));
//		 p.setVelocity(new Vector(p.getVelocity().getY(), 1.8D, 0.0D));
//		 p.setVelocity(new Vector(p.getVelocity().getY(), p.getLocation().getDirection().getY(), 0.0D ));
	}
}
