$ErrorActionPreference = 'Continue'
$srcDir = "F:\CreateTweakedControllers\src"

Get-ChildItem -Recurse -Filter "*.java" -Path $srcDir | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $original = $content
    
    # Simple package renames - Forge to NeoForge
    $content = $content -replace 'net\.minecraftforge\.eventbus\.api', 'net.neoforged.bus.api'
    $content = $content -replace 'net\.minecraftforge\.api\.distmarker', 'net.neoforged.api.distmarker'
    $content = $content -replace 'net\.minecraftforge\.fml\.javafmlmod\.FMLJavaModLoadingContext', 'net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext'
    $content = $content -replace 'net\.minecraftforge\.fml\.common\.Mod', 'net.neoforged.fml.common.Mod'
    $content = $content -replace 'net\.minecraftforge\.fml\.ModLoadingContext', 'net.neoforged.fml.ModLoadingContext'
    $content = $content -replace 'net\.minecraftforge\.fml\.ModList', 'net.neoforged.fml.ModList'
    $content = $content -replace 'net\.minecraftforge\.fml\.ModContainer', 'net.neoforged.fml.ModContainer'
    $content = $content -replace 'net\.minecraftforge\.fml\.LogicalSide', 'net.neoforged.fml.LogicalSide'
    $content = $content -replace 'net\.minecraftforge\.fml\.DistExecutor', 'net.neoforged.fml.DistExecutor'
    $content = $content -replace 'net\.minecraftforge\.fml\.event\.lifecycle', 'net.neoforged.fml.event.lifecycle'
    $content = $content -replace 'net\.minecraftforge\.fml\.config\.ModConfig', 'net.neoforged.fml.config.ModConfig'
    $content = $content -replace 'net\.minecraftforge\.registries\.DeferredRegister', 'net.neoforged.neoforge.registries.DeferredRegister'
    $content = $content -replace 'net\.minecraftforge\.registries\.RegistryObject', 'net.neoforged.neoforge.registries.DeferredHolder'
    $content = $content -replace 'net\.minecraftforge\.common\.MinecraftForge', 'net.neoforged.neoforge.common.NeoForge'
    $content = $content -replace 'net\.minecraftforge\.common\.ForgeMod', 'net.neoforged.neoforge.common.NeoForgeMod'
    $content = $content -replace 'net\.minecraftforge\.common\.ForgeConfigSpec', 'net.neoforged.neoforge.common.ModConfigSpec'
    $content = $content -replace 'net\.minecraftforge\.items\.ItemStackHandler', 'net.neoforged.neoforge.items.ItemStackHandler'
    $content = $content -replace 'net\.minecraftforge\.common\.capabilities\.Capability', 'net.neoforged.neoforge.capabilities.Capability'
    $content = $content -replace 'net\.minecraftforge\.common\.capabilities\.CapabilityManager', 'net.neoforged.neoforge.capabilities.CapabilityManager'
    $content = $content -replace 'net\.minecraftforge\.common\.capabilities\.CapabilityToken', 'net.neoforged.neoforge.capabilities.CapabilityToken'
    $content = $content -replace 'net\.minecraftforge\.common\.util\.LazyOptional', 'net.neoforged.neoforge.common.util.LazyOptional'
    $content = $content -replace 'net\.minecraftforge\.common\.util\.NonNullSupplier', 'net.neoforged.neoforge.common.util.NonNullSupplier'
    $content = $content -replace 'net\.minecraftforge\.client\.extensions\.common\.IClientItemExtensions', 'net.neoforged.neoforge.client.extensions.common.IClientItemExtensions'
    $content = $content -replace 'net\.minecraftforge\.network\.NetworkHooks', 'net.neoforged.neoforge.network.NetworkHooks'
    $content = $content -replace 'net\.minecraftforge\.client\.ConfigScreenHandler', 'net.neoforged.neoforge.client.ConfigScreenHandler'
    $content = $content -replace 'net\.minecraftforge\.client\.event', 'net.neoforged.neoforge.client.event'
    $content = $content -replace 'net\.minecraftforge\.event\.TickEvent', 'net.neoforged.neoforge.event.tick.TickEvent'
    $content = $content -replace 'net\.minecraftforge\.event\.entity', 'net.neoforged.neoforge.event.entity'
    $content = $content -replace 'net\.minecraftforge\.network', 'net.neoforged.neoforge.network'
    
    # Catch-all for any remaining net.minecraftforge references
    $content = $content -replace 'net\.minecraftforge\.fml', 'net.neoforged.fml'
    $content = $content -replace 'net\.minecraftforge', 'net.neoforged.neoforge'
    
    # Replace class name references
    $content = $content -replace '\bForgeConfigSpec\b', 'ModConfigSpec'
    $content = $content -replace '\bRegistryObject\b', 'DeferredHolder'
    $content = $content -replace '\bForgeMod\b', 'NeoForgeMod'
    
    if ($content -ne $original) {
        Set-Content $_.FullName $content -NoNewline
        Write-Host "Updated: $($_.Name)"
    }
}
Write-Host "Done with batch replacements"
