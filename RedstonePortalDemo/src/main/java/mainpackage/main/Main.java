package mainpackage.main;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public boolean isSame(ItemStack it1, ItemStack it2) {
        if (it1 != null) {
            if (it1.getItemMeta() != null) {
                if (it1.getItemMeta().getDisplayName().equals(it2.getItemMeta().getDisplayName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack portal(){
        ItemStack itemStack = new ItemStack(Material.END_CRYSTAL);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Portal");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.getInventory().addItem(portal());
    }

    Block block;

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if(isSame(player.getInventory().getItemInMainHand(),portal())){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                block = e.getClickedBlock();
                new BukkitRunnable(){
                    //Portalın otomatik kapanmasını kontrol etmek için saya.
                    int counter = 0;

                    @Override
                    public void run() {
                        ++counter;

                        Location loc = block.getLocation();

                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.PURPLE,1.0F);
                        player.getWorld().spawnParticle(Particle.REDSTONE,loc,2000,1.0,2.0,1.0,dustOptions);

                        if(counter==100){
                            player.sendMessage(ChatColor.AQUA + "Portal kapatıldı!");
                            this.cancel();
                        }
                    }
                }.runTaskTimer(this,0,3);
            }
        }
        new BukkitRunnable(){
            boolean isFindBlock = false;
            @Override
            public void run() {
                if(block!=null){
                    //Oyuncu için kordinatlar
                    int x = player.getLocation().getBlockX();
                    int y = player.getLocation().getBlockY();
                    int z = player.getLocation().getBlockZ();

                    //Blok için kordinatlar
                    int blockX = block.getLocation().getBlockX();
                    int blockY = block.getLocation().getBlockY()+1;
                    int blockZ = block.getLocation().getBlockZ();

                    Location loc = new Location(player.getWorld(),x,y,z);
                    Location blockLocation = new Location(player.getWorld(),blockX,blockY,blockZ);

                    if(loc.equals(blockLocation)){
                        isFindBlock = true;
                        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_VILLAGER_CELEBRATE,3.0F,0.0F);
                    }
                    if(isFindBlock){
                        //Sunucumdaki bir köydeki rastgele ev. Verdiğim kordinat önemli değil.
                        Location villageHouseLocation = new Location(player.getWorld(),-339,112,16);
                        player.teleport(villageHouseLocation);
                        player.sendMessage(ChatColor.GREEN + "Eve ışınlandınız!");
                        player.getWorld().playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_PLACE,3.0F,0.0F);

                        isFindBlock = false;
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(this,0,2);
    }
}
//-339 112 16
