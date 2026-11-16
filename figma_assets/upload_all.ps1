$files = @(
  @{f='D:/AndroidStudioProjects/figma_assets/01_Design_System.svg'; u='https://mcp.figma.com/mcp/upload/5d60f146-99c7-4b54-ab6d-57c03dd63b1e/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/02_Home_Menu.svg'; u='https://mcp.figma.com/mcp/upload/b7eb777d-4d62-4dff-bdcc-04455f38d4ee/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/03_Filtros_Modal.svg'; u='https://mcp.figma.com/mcp/upload/dcb2fc12-1aca-428c-8cf9-9f91df44bc34/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/04_Busqueda_Inicial.svg'; u='https://mcp.figma.com/mcp/upload/246f6a53-57be-4c44-b008-d944e352094f/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/05_Busqueda_Resultados.svg'; u='https://mcp.figma.com/mcp/upload/f93fafb5-9994-4402-bbe2-1457bdfe0cc6/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/06_Busqueda_Vacio.svg'; u='https://mcp.figma.com/mcp/upload/c1ccb3c7-a18b-49c2-939f-0bafa2f05d32/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/07_Detalle_Producto.svg'; u='https://mcp.figma.com/mcp/upload/f2965483-0a27-4a7c-bb2b-3d16184712a4/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/08_Cuenta_Invitado.svg'; u='https://mcp.figma.com/mcp/upload/bc117473-d403-4195-87ed-150ea90136fc/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/09_Cuenta_Login.svg'; u='https://mcp.figma.com/mcp/upload/7a1691fa-4cca-495b-bb84-19f78ead733c/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/10_Cuenta_Registro.svg'; u='https://mcp.figma.com/mcp/upload/6c320bba-b542-47c8-9109-e35fb689bf17/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/11_Cuenta_Perfil.svg'; u='https://mcp.figma.com/mcp/upload/b3886e03-1ce8-46c2-b1e0-90b373762621/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/12_Cuenta_Modal_Logout.svg'; u='https://mcp.figma.com/mcp/upload/d2cf9eeb-ab3a-46be-901c-e1a74e151e95/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/13_Recibos_Lista.svg'; u='https://mcp.figma.com/mcp/upload/554711c0-c501-429a-be63-aa0470788ec9/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/14_Recibos_Detalle.svg'; u='https://mcp.figma.com/mcp/upload/16c9f7a3-f2e2-4d20-be38-1306d0b4e5a3/submit?scaleMode=FILL&currentPageId=0%3A1'},
  @{f='D:/AndroidStudioProjects/figma_assets/15_Recibos_Vacio.svg'; u='https://mcp.figma.com/mcp/upload/c23f4ddb-491b-4202-a66e-a7903830a400/submit?scaleMode=FILL&currentPageId=0%3A1'}
)

foreach ($item in $files) {
  $file = $item.f
  $url = $item.u
  $res = & curl.exe -s -F "file=@$file" "$url"
  Write-Output "Result for $file : $res"
}
