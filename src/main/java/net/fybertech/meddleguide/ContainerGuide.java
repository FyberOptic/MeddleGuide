package net.fybertech.meddleguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.EnumContainerAction;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ContainerGuide extends ContainerChest
{
	int screenNum = 0;
	int pageNum = 0;
	int recipeNum = 0;
	
	//List<ShapedRecipes> shapedRecipes = new ArrayList<>();
	List<RecipeHolder> recipes = new ArrayList<>();
	
	Item slimeball;
	Item beef;
	Item apple;
	Item redstoneBlock;
	
	String[] scroll = new String[] {
			"         111 111 111  111 111    1 111 111 1 111",
			"         1   1 1  1   1 1  1     1 1 1 1   1 1  ",
			"         11  111  1   111  1     1 1 1 11    111",
			"         1   1 1  1   1 1  1     1 1 1 1       1",
			"         111 1 1  1   1 1  1   111 111 111   111"
	};
	
	class RecipeHolder
	{
		ItemStack result;
		ItemStack[] grid;
		boolean shapeless = false;
		
		public RecipeHolder(ItemStack result, ItemStack[] grid)
		{
			this.result = result;
			this.grid = grid;
		}
		
		public RecipeHolder(ItemStack result, ItemStack[] grid, boolean shapeless)
		{
			this.result = result;
			this.grid = grid;
			this.shapeless = shapeless;
		}
	}
	
	
	private ItemStack fixItemStack(ItemStack stack)
	{
		if (stack == null) return null;
		else if ((stack.getItem() instanceof ItemBlock) && stack.getMetadata() == 32767) return new ItemStack(stack.getItem());
		else if (stack.stackSize > 1) return new ItemStack(stack.getItem(), 1, stack.getItemDamage());
		else return stack;
	}
	
	
	private boolean matchesSearch(ItemStack stack, String[] search)
	{
		if (search == null || stack == null || search.length < 1) return true;
		
		String unlocalizedName = stack.getUnlocalizedName().toLowerCase();
		String displayName = stack.getDisplayName().toLowerCase();
		
		boolean result = false;
		for (String s : search) {
			s = s.toLowerCase();
			if (unlocalizedName.contains(s) || displayName.contains(s)) result = true;
		}		
		return result;		
	}
	
	
	public ContainerGuide(IInventory param0, IInventory param1, EntityPlayer param2, String[] search) 
	{
		super(param0, param1, param2);
	
		slimeball = Item.getByNameOrId("slime_ball");
		beef = Item.getByNameOrId("cooked_beef");
		apple = Item.getByNameOrId("apple");
		redstoneBlock = Item.getByNameOrId("redstone_block");
		
		List<IRecipe> handlers = CraftingManager.getInstance().getRecipeList();
		for (IRecipe handler : handlers) 
		{
			if (handler instanceof ShapedRecipes) 
			{
				ShapedRecipes shaped = (ShapedRecipes)handler;
				ItemStack[] grid = new ItemStack[9];
				
				//System.out.println("Output: " + shaped.recipeOutput.getDisplayName());
				for (int y = 0; y < 3; y++)	{
					if (y >= shaped.recipeHeight) break;					
					for (int x = 0; x < 3; x++) {
						if (x >= shaped.recipeWidth) break;
						//System.out.println(shaped.recipeItems[x + (y * shaped.recipeWidth)]);
						grid[x + (y * 3)] = fixItemStack(shaped.recipeItems[x + (y * shaped.recipeWidth)]);
					}
				}
				//System.out.println("");
				
				if (matchesSearch(shaped.recipeOutput, search)) 
					recipes.add(new RecipeHolder(shaped.recipeOutput, grid));
			}
			else if (handler instanceof ShapelessRecipes) 
			{
				ShapelessRecipes shapeless = (ShapelessRecipes)handler;
				
				ItemStack[] grid = new ItemStack[9];
				int size = shapeless.recipeItems.size();
				if (size > 9) size = 9;
				for (int n = 0; n < size; n++) {
					grid[n] = fixItemStack(shapeless.recipeItems.get(n));
				}
				
				if (matchesSearch(shapeless.recipeOutput, search)) 
					recipes.add(new RecipeHolder(shapeless.recipeOutput, grid, true));			
			}
		}
		
		if (recipes.size() == 1) screenNum = 1; 
		
		updateScreen();
	}
	
	
	int counter = 0;
	
	/*@Override
	public void detectAndSendChanges() 
	{	
		counter++;
		if (counter > 3) {
			counter -= 3;
			updateTicker();
		}
		
		super.detectAndSendChanges();
	}*/
	
	int xPos = -1;
	
	public void updateTicker()
	{
		xPos++;
		if (xPos > scroll[0].length()) xPos = 0;
		
		for (int n = 0; n < 9 * 6; n++) {
			super.putStackInSlot(n, null);
		}
		
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 9; x++) {
				String rowString = scroll[y];
				
				if (xPos + x < rowString.length() && rowString.charAt(xPos + x) == '1')
					super.putStackInSlot(x + (y * 9), new ItemStack(redstoneBlock));
				//else putStackInSlot(x + (y * 9), new ItemStack(slimeball));
			}
		}		
	}
	
	
	
	public void updateScreen()
	{
		for (int n = 0; n < 9 * 6; n++) {
			super.putStackInSlot(n, null);
		}
		
		switch (screenNum)
		{
			case 0: 
			{				
				int max = 9 * 5;
				int startNum = pageNum * max;
				int recipeCount = recipes.size();
				int maxPage = (int)(Math.ceil(recipeCount / (float)max));
				
				for (int pos = 0; pos < max; pos++) {
					if (recipes.size() <= startNum + pos) break;
					super.putStackInSlot(pos, recipes.get(startNum + pos).result);
				}
				
				String[] page = new String[] {"Current page: " + (pageNum+1) + " of " + maxPage};
				super.putStackInSlot(48, getStackWithTag("Prev", page, slimeball));
				super.putStackInSlot(50, getStackWithTag("Next", page, slimeball));
			}
			break;
			
			case 1:
			{
				RecipeHolder holder = recipes.get(recipeNum);
				
				for (int y = 0; y < 3; y++) {
					for (int x = 0; x < 3; x++) {
						super.putStackInSlot((x+3) + ((y+1) * 9), holder.grid[x + (y * 3)]);						
					}
				}
				
				String[] info = new String[] { "Recipe: " + holder.result.getDisplayName(), "Shapeless: " + holder.shapeless };
				super.putStackInSlot(49, getStackWithTag("Back", info, slimeball));
			}
		}
			
		
		detectAndSendChanges();
	}
	
	
	private ItemStack getStackWithTag(String name, Item item)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound display = new NBTTagCompound();
		display.setString("Name", name);
		tag.setTag("display", display);
		
		ItemStack stack = new ItemStack(item);		
		stack.setTagCompound(tag);
		return stack;
	}
	
	private ItemStack getStackWithTag(String name, String[] lore, Item item)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound displayTag = new NBTTagCompound();			
		displayTag.setString("Name", name);
		
		if (lore != null && lore.length > 0) {
			NBTTagList loreTag = new NBTTagList();
			for (String l : lore) loreTag.appendTag(new NBTTagString(l));	
			displayTag.setTag("Lore", loreTag);			
		}
		
		tag.setTag("display", displayTag);
		
		ItemStack stack = new ItemStack(item);		
		stack.setTagCompound(tag);
		return stack;
	}	
	
	
	private void onClickedGrid(int x, int y, int button, EntityPlayer player)
	{		
		int num = (y * 9) + x;
		
		if (screenNum == 0 && y < 5) {		
			
			recipeNum = pageNum * (9 * 5) + num;
			if (recipeNum >= recipes.size()) return;
		
			screenNum = 1;			
			updateScreen();
		}
		else if (screenNum == 0 && num == 48) {
			pageNum--;
			if (pageNum < 0) pageNum = 0;
			updateScreen();
		}
		else if (screenNum == 0 && num == 50) {
			pageNum++;
			if (pageNum * (9 * 5) > recipes.size()) pageNum--;
			updateScreen();
		}
		else if (screenNum == 1 && num == 49) {
			screenNum = 0;
			updateScreen();			
		}
		
	}
	
	
	
	@Override
	public ItemStack slotClick(int id, int button, EnumContainerAction mode, EntityPlayer player) 
	{
		int col = id % 9;
		int row = id / 9;
		
		if (button == 0 && mode.ordinal() == 0) onClickedGrid(col, row, button, player);		
		else if (mode.ordinal() != 0) {
			// TODO - Handle more efficiently than sending entire inventory
			for (int n = 0; n < this.inventorySlots.size(); n++) {
				for (ICrafting crafter : this.crafters) {
					crafter.sendSlotContents(this, n, this.getSlot(n).getStack());
				}
			}
		}		
		
		//System.out.println("SLOT CLICK: " + col + " " + id + " " + button + " " + mode + " " + player);
		return null;
	}
	

	
	
}
