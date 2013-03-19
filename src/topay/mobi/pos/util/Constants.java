package topay.mobi.pos.util;

public interface Constants {
	// public static final String URL_BASE = "http://10.0.2.2:8080/VostuGateway/";
	 public static final String URL_BASE = "http://107.22.181.155:8080/Gateway/";

	// public static final String URL_BASE = "http://192.168.100.101:8080/Gateway/";

	//public static final String URL_BASE = "http://192.168.0.64:8080/Gateway/";

	public static final String LISTAR_CHECKINS_URL = URL_BASE + "ListarCheckins";
	public static final String LISTAR_CHECKOUTS_URL = URL_BASE + "ListarCheckouts";
	public static final String DETALHES_CHECKIN_URL = URL_BASE + "DetalhesClienteExperienciaServlet";
	public static final String ENVIA_DETALHES_PAGAMENTO = URL_BASE + "EnviaDetalhesPagamento";
	public static final String ENVIA_CLIENT_NUMBER = URL_BASE + "EnviarClientNumber";
	public static final String REMOVE_CLIENTE_PAYMENT=URL_BASE+"RemoveClienteServlet";

	public static final String HOST_SERVER_URL = "https://topaymobi.herokuapp.com/";

	public static final String WISHLIST_OBJECTS_URL[] = { HOST_SERVER_URL + "goodExperience.php", HOST_SERVER_URL + "badExperience.php", HOST_SERVER_URL + "regularExperience.php" };

	public static final String ESTABELECIMENTO_ID = "c2fb479d014b0a579e87a1f9e95617b364fd2222";
	
//	"c4fb479d014b0a579e87a1f9e95617b364fd3698"
//	"a0fb479d014b0a579e87a1f9e95617b364fd0000"
//	"b1fb479d014b0a579e87a1f9e95617b364fd1111"
//	"c2fb479d014b0a579e87a1f9e95617b364fd2222"
}
