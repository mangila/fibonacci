import type { SseSubscription } from "../_types/types";

interface Props {
  subscription: SseSubscription;
}

export const ChannelCard = ({ subscription }: Props) => {
  return (
    <>
      <>
        <div className="stat">
          <div className="stat-title">Channel </div>
          <div className="stat-value">{subscription.channel}</div>
        </div>
      </>
    </>
  );
};
